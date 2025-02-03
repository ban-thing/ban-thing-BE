import sys
import os
import re

from io import StringIO
import json
import pandas as pd
from datetime import datetime
import time

import torch

from sklearn.cluster import KMeans
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.decomposition import PCA

from sentence_transformers import SentenceTransformer, models


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
    X_full = model.encode([input_hashtag], convert_to_tensor=True) 
    X_full.to(device)

    # Find the best matches for each question with detailed information
    return X_full

print(vectorized_hashtag("파란색, 별, 가슴", 'All-MiniLM-L6-v2'))