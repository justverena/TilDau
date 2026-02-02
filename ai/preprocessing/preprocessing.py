import os
import soundfile as sf

from audio_io import load_audio
from normalization import normalize_audio
from features import trim_silence, compute_basic_features

def process_file(input_path, output_path):
    audio, sr = load_audio(input_path)

    trimmed = trim_silence(audio, sr)
    normalized = normalize_audio(trimmed)

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    sf.write(output_path, normalized, sr)

    features = compute_basic_features(audio=audio, trimmed_audio=trimmed, sr=sr)

    return features