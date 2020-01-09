import pyaudio
import wave

CHUNK = 1024
FORMAT = pyaudio.paInt16
CHANNELS = 2
RATE = 44100
WINDOW_SIZE = 1

def record():
    p = pyaudio.PyAudio()
    stream = p.open(format=FORMAT,
                    channels=CHANNELS,
                    rate=RATE,
                    input=True,
                    frames_per_buffer=CHUNK)
    print("Start recording")
    frames = []
    wholeframes = []
    jth_audio =1
    try:
        while True:
            for i in range(0, int(RATE / CHUNK * WINDOW_SIZE)):
                data = stream.read(CHUNK)
                frames.append(data)
                wholeframes.append(data)
            waveFile = wave.open("Number " + str(jth_audio) + " audio segment.wav", 'wb')
            waveFile.setnchannels(CHANNELS)
            waveFile.setsampwidth(p.get_sample_size(FORMAT))
            waveFile.setframerate(RATE)
            waveFile.writeframes(b''.join(frames))
            waveFile.close()
            jth_audio = jth_audio + 1
            frames =[]
    except KeyboardInterrupt:
        print("Done recording")
    except Exception as e:
        print(str(e))
    sample_width = p.get_sample_size(FORMAT)
    stream.stop_stream()
    stream.close()
    p.terminate()
    return sample_width, frames, wholeframes


def record_to_file(file_path):
    wf = wave.open(file_path, 'wb')
    wf.setnchannels(CHANNELS)
    sample_width, frames , complete_audio = record()
    wf.setsampwidth(sample_width)
    wf.setframerate(RATE)
    wf.writeframes(b''.join(frames))
    wf.close()
    wf = wave.open("whole audio", 'wb')
    wf.setnchannels(CHANNELS)
    wf.setsampwidth(sample_width)
    wf.setframerate(RATE)
    wf.writeframes(b''.join(complete_audio))
    wf.close()

if __name__ == '__main__':
    print('#' * 80)
    print("Please speak word(s) into the microphone")
    print('Press Ctrl+C to stop the recording')
    record_to_file('last audio segment.wav')
    print("Result written to output.wav")
    print('#' * 80)