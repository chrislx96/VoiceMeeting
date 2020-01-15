import wave
from pynput import keyboard
import pyaudio
import argparse
import threading

# ===========================================
#        Parse the argument
# ===========================================
parser = argparse.ArgumentParser()
# setup pyaudio record configuration
parser.add_argument('--CHUNK', default=1024, type=int)
parser.add_argument('--FORMAT', default=pyaudio.paInt16)
parser.add_argument('--CHANNELS', default=1, type=int)
parser.add_argument('--RATE', default=16000, type=int)
parser.add_argument('--RECORD_SECONDS', default=5, type=int)
parser.add_argument('--FILENAME', default='output.wav', type=str)
args = parser.parse_args()


class MyListener(keyboard.Listener):
    def __init__(self):
        super(MyListener, self).__init__(self.on_press)
        self.start_flag = False
        self.end_flag = False

    def on_press(self, key):
        # start recording
        if key == keyboard.Key.space:
            self.start_flag = True
            print('Start recording.')

        # stop recording
        elif key == keyboard.Key.esc:
            print('Stop recording')
            self.end_flag = True
            return False

        return True


class AudioRecorder:
    def __init__(self, frames):
        super().__init__()
        self.frames = frames
        self.p = pyaudio.PyAudio()
        self.listener = MyListener()
        self.stream = None
        self.wf = wave.open(args.FILENAME, 'wb')
        self.wf.setnchannels(args.CHANNELS)
        self.wf.setsampwidth(self.p.get_sample_size(args.FORMAT))
        self.wf.setframerate(args.RATE)

    def start_record(self):
        while not self.listener.start_flag:
            pass

        self.stream = self.p.open(format=args.FORMAT,
                                  channels=args.CHANNELS,
                                  rate=args.RATE,
                                  input=True,
                                  frames_per_buffer=args.CHUNK)
        # stream_callback=self.callback)
        self.stream.start_stream()
        while self.listener.start_flag:
            data = self.stream.read(args.CHUNK)
            self.frames.append(data)

            if self.listener.end_flag:
                self.stream.stop_stream()
                self.stream.close()
                self.p.terminate()
                self.wf.writeframes(b''.join(self.frames))
                self.wf.close()
                print('You should have a wav file in the current directory')
                break

    def start_keyboard_listener(self):
        self.listener.start()
        print("Press 'space bar' to start recording.")
        print("Press 'Esc' to end recording.")


if __name__ == '__main__':
    frames = []
    recorder = AudioRecorder(frames)
    recorder.start_keyboard_listener()
    recorder.start_record()
    print('End.')
    print(frames)
