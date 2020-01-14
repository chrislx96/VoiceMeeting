import wave
from pynput import keyboard
import pyaudio
import argparse

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
        super(MyListener, self).__init__(self.on_press, self.on_release)
        self.key_pressed = None
        self.wf = wave.open(args.WAVE_OUTPUT_FILENAME, 'wb')
        self.wf.setnchannels(args.CHANNELS)
        self.wf.setsampwidth(p.get_sample_size(args.FORMAT))
        self.wf.setframerate(args.RATE)

    def on_press(self, key):
        # start recording
        if key == keyboard.Key.space:
            print('+++++++++++++++++')

        # stop recording
        elif key == keyboard.Key.esc:
            print('-----------------')
        # stop listener
        elif key.char == 'q':
            return False

        return True

    def on_release(self, key):
        return True


if __name__ == '__main__':
    p = pyaudio.PyAudio()
