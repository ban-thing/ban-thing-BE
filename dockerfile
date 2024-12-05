# Use an official Python runtime as a base image
FROM python:3.9.11-slim

# Copy the application code to the container
COPY . /app

# Set the working directory inside the container
WORKDIR /app

# Install Python dependencies
RUN pip install --upgrade pip
RUN pip install pandas
RUN pip install numpy
RUN pip install torch
RUN pip install flask
RUN pip install scikit-learn
RUN pip install sentence-transformers
RUN ls

# Expose the port the app runs on (optional)
EXPOSE 7000

# Set environment variables (optional)
ENV PYTHONUNBUFFERED=1

# Run the application
CMD ["python3", "python/main__.py"]
