import os
import numpy as np
import torch
import librosa
from tqdm import tqdm
from transformers import Wav2Vec2Processor, Wav2Vec2Model, Wav2Vec2FeatureExtractor

base_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.abspath(os.path.join(base_dir, "../../"))

audio_dir = os.path.join(project_root, "data/dataset-processed/audio")
output_dir = os.path.join(project_root, "data/reference_embeddings")
output_path = os.path.join(output_dir, "wav2vec2_reference_mean.npy")

target_sr = 16000
model_name = "facebook/wav2vec2-xls-r-300m"

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

feature_extractor = Wav2Vec2FeatureExtractor.from_pretrained(model_name)
model = Wav2Vec2Model.from_pretrained(model_name)

model.to(device) # type: ignore
model.eval()


def load_audio(path: str, sr: int = target_sr) -> np.ndarray:
    audio, _ = librosa.load(path, sr=sr, mono=True)
    return audio

def extract_embedding(audio: np.ndarray) -> np.ndarray:
    inputs = feature_extractor(
        audio,
        sampling_rate=target_sr,
        return_tensors="pt",
        padding=True
    )

    inputs = {k: v.to(device) for k, v in inputs.items()}

    with torch.no_grad():
        outputs = model(**inputs)

    embedding = outputs.last_hidden_state.mean(dim=1).squeeze(0)
    return embedding.cpu().numpy()

def main():
    embeddings = []

    wav_files = [
        f for f in os.listdir(audio_dir)
        if f.lower().endswith(".wav")
    ]

    print(f"Found {len(wav_files)} audio files")

    for fname in tqdm(wav_files, desc="Building reference embeddings"):
        path = os.path.join(audio_dir, fname)

        try:
            audio = load_audio(path)
            emb = extract_embedding(audio)
            embeddings.append(emb)
        except Exception as e:
            print(f"Failed on {fname}: {e}")

    embeddings = np.stack(embeddings)
    reference_mean = embeddings.mean(axis=0)

    os.makedirs(output_dir, exist_ok=True)
    np.save(output_path, reference_mean)

    print("\n Reference embedding saved in")
    print(output_path)
    print("Embedding shape:", reference_mean.shape)


if __name__ == "__main__":
    main()