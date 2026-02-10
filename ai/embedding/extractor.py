import torch
import numpy as np
from embedding.model import model, feature_extractor

def extract_embedding(audio: np.ndarray, sr: int) -> np.ndarray:
    inputs = feature_extractor(
        audio,
        sampling_rate=sr,
        return_tensors="pt",
        padding=True
    )

    input_values = inputs["input_values"].to(model.device)

    with torch.no_grad():
        outputs = model(input_values)
    
    embedding = outputs.last_hidden_state.mean(dim=1)
    return embedding.cpu().numpy()