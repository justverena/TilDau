from audio.utils import download_audio
from preprocessing.runtime import runtime_preprocess

path = "/var/folders/18/tvmsghm137n82rm165_hbqcm0000gn/T/tmpa2i3yxyc.wav"
#path = download_audio(URL)
audio, basic_features = runtime_preprocess(path, sr=16000)

print(audio.shape)
print(basic_features)