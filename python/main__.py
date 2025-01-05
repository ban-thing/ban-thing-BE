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

def adv_search(question, trait_data, model_name, n_components):

    # Check if CUDA is available and set the device accordingly
    device = 'cuda' if torch.cuda.is_available() else 'cpu'

    trait_data.head()

    # 모든 텍스트 소문자로 전환하기


    # 행동 특성의 이름과 질문 텍스트 소문자화
    trait_data['Processed_Hashtag'] = trait_data['hashtag']

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

    # 해시태그 데이터 vectorization
    X_full = model.encode(trait_data['Processed_Hashtag'].tolist(), convert_to_tensor=True) 
    X_full = X_full.to(device)

    # 질문 텍스트 vectorization 적용한 후 

    # Function to find best matches in dataset based on the processed text and include rank
    def match_question_to_data_detailed(question, trait_data, top_n=None):
        
        # 질문 텍스트 vectorization 
        question_vec = model.encode([question], convert_to_tensor=True)
        
        # If there are enough samples, apply PCA
        if question_vec.shape[0] > 1:
            pca = PCA(n_components)
            question_vec = pca.fit_transform(question_vec.cpu().numpy())
        else:
            question_vec = question_vec.cpu().numpy()[:, :50]
        question_vec = question_vec.to(device)
        
        # 각가의 질문과 행동 특성 사이의 유사도(similarity) 계산
        cosine_similarities = cosine_similarity(question_vec, X_full).flatten()  # Compute cosine similarity on CPU
        
        # 유사도(similarity)가 높은 순서대로 결과 정렬
        if top_n:
            related_docs_indices = cosine_similarities.argsort()[-top_n:][::-1]
        else:
            related_docs_indices = cosine_similarities.argsort()[::-1]
        matched_data = trait_data.iloc[related_docs_indices]
        matched_data['Question']= question
        matched_data['Matching Rank/Probability'] = cosine_similarities[related_docs_indices]
        return matched_data

    # Find the best matches for each question with detailed information
    question_matches_detailed_ranked = {}
    for idx, q in enumerate([question]):
        matched_data = match_question_to_data_detailed(q, trait_data) # 각각의 질문 내용과 행동 특성들을 비교
        question_matches_detailed_ranked[f"advanced_search_result {idx}"] = matched_data

    #output_filepath_questions_detailed_ranked = r"advanced_search_result.xlsx"
    #writer = pd.ExcelWriter(output_filepath_questions_detailed_ranked, engine='xlsxwriter')
    for q_key, matched_data in question_matches_detailed_ranked.items():
        # Add the question as a separate row before the matches
        dataframe = pd.DataFrame([[None, None, None, None, None, None, None, None, None]], columns=['id', 'updatedAt', 'address', 'price', 'title', 'type', 'hashtag', 'images', 'Matching Rank/Probability'])
        dataframe = pd.concat([dataframe, matched_data], ignore_index=True)
        #dataframe.to_excel(writer, sheet_name=q_key, index=False)

    # Use close() instead of save()
    #writer.close()

    return dataframe

def dict_to_String(hashtag_list):
    return ", ".join(hashtag_list)

@app.route("/post", methods=['POST'])
def advanced_search():
    # get the data from dataset with label '해시태그'
    body = request.json
    hashtag = body['input_hashtag']
    items = body['items']
    
    response_df = pd.DataFrame(items)
    response_df['hashtag'] = response_df['hashtag'].apply(dict_to_String)

    ## other models
    model_name = 'paraphrase-albert-small-v2'
                 

    dimensions = [50,
                  70,
                  90,
                  110,
                  120]

    for dimension in dimensions: 
        
        start = time.time()
        
        df = adv_search(hashtag, response_df, model_name, dimension)

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

