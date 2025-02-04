import sys
import os
import re

from flask import Flask, jsonify, request
from http import HTTPStatus
from io import StringIO
import json
import pandas as pd
from datetime import datetime
import time

import torch

from sklearn.cluster import KMeans
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.decomposition import PCA

from transformers import AutoModel, AutoTokenizer
from sentence_transformers import SentenceTransformer, models


app = Flask(__name__)

def vectorized_hashtag(input_hashtag, model_name):
    # Check if CUDA is available and set the device accordingly
    device = 'cuda' if torch.cuda.is_available() else 'cpu'
    # 질문 텍스트 vectorization

    # 모델 불러오기
    if model_name == 'monologg/koalbert-tiny':
        # Define the transformer and pooling layers
        word_embedding_model = models.Transformer(model_name, max_seq_length=128)
        pooling_model = models.Pooling(
        word_embedding_model.get_word_embedding_dimension(),
        pooling_mode_mean_tokens=True,
        pooling_mode_cls_token=False,
        pooling_mode_max_tokens=False,
        )

        # Combine into a Sentence Transformer model
        model = SentenceTransformer(modules=[word_embedding_model, pooling_model])
        
    else:
        # sentence transformer 모델 불러오기
        model = SentenceTransformer(model_name) # sentence transfomers 모델 불러오기
        model = model.to(device)  

    # 해시태그 데이터 vectorization
    result = model.encode(input_hashtag, convert_to_tensor=True) 
    result.to(device)

    # Find the best matches for each question with detailed information
    return result

def adv_search(hashtags, response_df, model_name):

    # Check if CUDA is available and set the device accordingly
    device = 'cuda' if torch.cuda.is_available() else 'cpu'
    # 질문 텍스트 vectorization

    if model_name == 'monologg/koalbert-tiny':
        # Define the transformer and pooling layers
        word_embedding_model = models.Transformer(model_name, max_seq_length=128)
        pooling_model = models.Pooling(
        word_embedding_model.get_word_embedding_dimension(),
        pooling_mode_mean_tokens=True,
        pooling_mode_cls_token=False,
        pooling_mode_max_tokens=False,
        )

        # Combine into a Sentence Transformer model
        model = SentenceTransformer(modules=[word_embedding_model, pooling_model])
        
    else:
        # sentence transformer 모델 불러오기
        model = SentenceTransformer(model_name) # sentence transfomers 모델 불러오기
        model = model.to(device)  

    response_df.to(device)

    n_samples1, n_features1 = response_df.numpy().shape
    
    # Function to find best matches in dataset based on the processed text and include rank
    def match_question_to_data_detailed(question, response_df, n_samples1, n_features1, top_n=None):
        
        # 질문 텍스트 vectorization 
        question_vec = model.encode([question], convert_to_tensor=True)
        question_vec.to(device)
    
        # If there are enough samples, apply PCA
        n_samples2, n_features2 = question_vec.numpy().shape

        n_components = min(n_samples1, n_features1, n_samples2, n_features2)
        pca = PCA(n_components=n_components)

        if n_components > 1:
            X_full = pca.fit_transform(X_full.numpy())
            question_vec = pca.fit_transform(question_vec.numpy())
        else:
            X_full = X_full.numpy()
            question_vec = question_vec.numpy()
        
        # 각가의 질문과 행동 특성 사이의 유사도(similarity) 계산
        cosine_similarities = cosine_similarity(question_vec, X_full).flatten()  # Compute cosine similarity on CPU
        
        # 유사도(similarity)가 높은 순서대로 결과 정렬
        if top_n:
            related_docs_indices = cosine_similarities.argsort()[-top_n:][::-1]
        else:
            related_docs_indices = cosine_similarities.argsort()[::-1]
        matched_data = response_df.iloc[related_docs_indices]
        matched_data['Question']= question
        matched_data['Matching Rank/Probability'] = cosine_similarities[related_docs_indices]
        return matched_data

    # Find the best matches for each question with detailed information
    question_matches_detailed_ranked = {}
    for idx, q in enumerate([hashtags]):
        matched_data = match_question_to_data_detailed(q, response_df, n_samples1, n_features1) # 각각의 질문 내용과 행동 특성들을 비교
        question_matches_detailed_ranked[f"advanced_search_result {idx}"] = matched_data

    #output_filepath_questions_detailed_ranked = r"advanced_search_result.xlsx"
    #writer = pd.ExcelWriter(output_filepath_questions_detailed_ranked, engine='xlsxwriter')
    for q_key, matched_data in question_matches_detailed_ranked.items():
        # Add the question as a separate row before the matches
        dataframe = pd.DataFrame([[None, None, None, None, None, None, None, None, None]], columns=['id', 'updatedAt', 'address', 'price', 'title', 'type', 'hashtag', 'images', 'Matching Rank/Probability', 'vectorized_hashtags'])
        dataframe = pd.concat([dataframe, matched_data], ignore_index=True)
        #dataframe.to_excel(writer, sheet_name=q_key, index=False)

    # Use close() instead of save()
    #writer.close()

    return dataframe

def dict_to_String(hashtag_list):
    return ", ".join(hashtag_list)

@app.route("/vectorization", methods=['POST'])
def vectorization():

    body = request.json

    if not isinstance(body['input_hashtag'], str):
        return jsonify({"error": "input_hashtag must be a string"}), 400

    print(f"Received input_hashtag: {body['input_hashtag']}")
    print(type(body))

    dict_body = dict()
    dict_body['input_hashtag'] = [body['input_hashtag']]

    response_df = pd.DataFrame(dict_body)
    response_df['input_hashtag'] = response_df['input_hashtag'].apply(dict_to_String)
    
    ## other models
    model_name = 'All-MiniLM-L6-v2'

    start = time.time()

    result = vectorized_hashtag(response_df['input_hashtag'], model_name)
    print(type(result))
    end = time.time()
    print(model_name, ": ", end - start, "초")

    return jsonify(pd.DataFrame(result.tolist()).to_dict(orient='records'))

@app.route("/post", methods=['POST'])
def advanced_search():
    # get the data from dataset with label '해시태그'
    body = request.json
    hashtag = body['input_hashtag']
    items = body['items']
    
    response_df = pd.DataFrame(items)
    response_df['vectorized_hashtags'] = response_df['vectorized_hashtags'].apply(dict_to_String)

    ## other models
    model_name = 'All-MiniLM-L6-v2'
    
    start = time.time()
    
    df = adv_search(hashtag, response_df, model_name)

    print("---------------------------------")
    print("::::::",model_name,"::::::")
    print(response_df.columns)
    print(response_df)
    print("---------------------------------")
    print(df.columns)
    print(df)
    print("---------------------------------") 

    df = df[df['Matching Rank/Probability'] > 0.1]
    df = df[['id', 'updatedAt', 'address', 'price', 'title', 'type', 'hashtag', 'images']]
    print(df.to_dict(orient='records'))

    end = time.time()
    print(model_name, ": ", end - start, "초")

    return jsonify(df.to_dict(orient='records'))

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=7000, debug = True)

