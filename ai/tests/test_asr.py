from audio.utils import download_audio
from asr.whisper_asr import transcribe_audio

path = "/var/folders/18/tvmsghm137n82rm165_hbqcm0000gn/T/tmpa2i3yxyc.wav"
#path = download_audio(URL)
text = transcribe_audio(path)

print(text)
