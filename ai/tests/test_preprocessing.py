from preprocessing.runtime import runtime_preprocess

path = "tests/sample.wav"
audio, basic_features = runtime_preprocess(path, sr=16000)

print(audio.shape)
print(basic_features)