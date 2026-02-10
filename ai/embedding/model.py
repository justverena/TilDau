from transformers import Wav2Vec2Model, Wav2Vec2FeatureExtractor
import torch

model_name = "facebook/wav2vec2-xls-r-300m"

feature_extractor = Wav2Vec2FeatureExtractor.from_pretrained(model_name)
model = Wav2Vec2Model.from_pretrained(model_name)
model.eval()