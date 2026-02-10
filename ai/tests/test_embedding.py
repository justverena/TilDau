import os
import librosa
import numpy as np
from embedding.extractor import extract_embedding
from embedding.similarity import cosine_similarity
from scroing.embedding_score import score_embedding

path = "/var/folders/18/tvmsghm137n82rm165_hbqcm0000gn/T/tmpa2i3yxyc.wav"

sr = 16000

audio, _ = librosa.load(path, sr=sr, mono=True)
user_embedding = extract_embedding(audio, sr)
print("User embedding shape:", user_embedding.shape)
print("User embedding (first 10 values):", user_embedding.flatten()[:10])

base_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.abspath(os.path.join(base_dir, "../../"))
reference_embedding_path = os.path.join(project_root, "data/reference_embeddings/wav2vec2_reference_mean.npy")
reference_embedding = np.load(reference_embedding_path)

sim = cosine_similarity(user_embedding, reference_embedding)
print("Cosine similarity:", sim)

score_dict = score_embedding(sim)
print("Embedding score dict:", score_dict)