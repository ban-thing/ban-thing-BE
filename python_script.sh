# Version: 20251223 Apple Silicon

#!/bin/bash

set -e
echo "반띵 백엔드 테스트 환경 구축 시작"

echo "Python 패키지 다운로드 시작"

sudo brew install python@3.9
pip install pandas
pip install numpy
pip install flask
pip install torch --no-cache-dir
pip install scikit-learn --no-cache-dir
pip install sentence-transformers --no-cache-dir

echo "Python 패키지 다운로드 성공"

echo "Flask 로컬 서버 실행"

python3 main__.py

echo "Flask 로컬 서버 실행 성공"
