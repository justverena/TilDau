import csv
import librosa

def load_audio(path, target_sr=16000):
    audio, sr = librosa.load(path, sr=target_sr, mono=True)
    return audio, target_sr

def load_raw_metadata(path):
    with open(path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        return list(reader)