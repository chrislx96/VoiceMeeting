"""PyAudio Example: Play a wave file."""

import pyaudio
import wave
import numpy as np
import librosa

CHUNK = 1024

# instantiate PyAudio (1)
p = pyaudio.PyAudio()

# open stream (2)
stream = p.open(format=p.get_format_from_width(2),
                channels=1,
                rate=16000,
                frames_per_buffer=CHUNK,
                input=True)

# read data
data = stream.read(CHUNK)
np_data = np.fromstring(data, dtype=np.int16).astype('float64')

linear1 = librosa.stft(np_data, n_fft=512, win_length=400, hop_length=160, center=True)  # linear spectrogram
linear1 = linear1.T
mag, _ = librosa.magphase(linear1)  # magnitude
np.savetxt('test1.txt', linear1, delimiter=',')

print('==============================')
tmp = np.pad(np_data[:256], (int(512 // 2), 0), mode='reflect')
linear2 = librosa.stft(tmp, n_fft=512, win_length=400, hop_length=160, center=False)
tmp = np.pad(np_data[:416], (96, 0), mode='reflect')
linear2 = np.append(linear2, librosa.stft(tmp, n_fft=512, win_length=400, hop_length=160, center=False), axis=1)

for i in range(64, 1024, 160):
    if i + 512 > 1024:
        reverse = np.flip(np_data[-257:-1])
        print(reverse.shape)
        tmp = np.append(np_data[i:], reverse)
        tmp = librosa.stft(tmp, n_fft=512, win_length=400, hop_length=160, center=False)
        linear2 = np.append(linear2, tmp, axis=1)
        break

    tmp = librosa.stft(np_data[i: i + 512], n_fft=512, win_length=400, hop_length=160, center=False)
    linear2 = np.append(linear2, tmp, axis=1)

np.savetxt('test2.txt', linear2, delimiter=',')
print(linear1.shape)
print(linear2.shape)

# for i in range(1024):

# print(np_data.shape)
# print(linear1.shape)

wf = wave.open('output.wav', 'wb')
wf.setnchannels(1)
wf.setsampwidth(2)
wf.setframerate(16000)
wf.writeframes(data)
wf.close()

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
