import librosa
import numpy as np
from embedding.extractor import extract_embedding
from embedding.model import model, feature_extractor

audio_path = "tests/sample.wav"
sr = 16000

audio, _ = librosa.load(audio_path, sr=sr, mono=True)

embedding = extract_embedding(audio, sr)

print("Embedding shape:", embedding.shape)
print("Embedding vector (first 10 values):", embedding.flatten()[:10])