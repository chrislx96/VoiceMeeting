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
parser.add_argument('--SAMPLE_WIDTH', default=2, type=int)
parser.add_argument('--CHANNELS', default=1, type=int)
parser.add_argument('--RATE', default=16000, type=int)
parser.add_argument('--RECORD_SECONDS', default=5, type=int)
parser.add_argument('--FILENAME', default='output.wav', type=str)
args = parser.parse_args()

frames = []


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
    def __init__(self):
        self.p = pyaudio.PyAudio()
        self.listener = MyListener()
        self.wf = wave.open(args.FILENAME, 'wb')
        self.wf.setnchannels(args.CHANNELS)
        self.wf.setsampwidth(args.SAMPLE_WIDTH)
        self.wf.setframerate(args.RATE)

    def start_record(self):
        stream = self.p.open(format=self.p.get_format_from_width(args.SAMPLE_WIDTH),
                             channels=args.CHANNELS,
                             rate=args.RATE,
                             input=True,
                             frames_per_buffer=args.CHUNK,
                             stream_callback=self.callback)
        stream.start_stream()
        while stream.is_active():
            if self.listener.end_flag:
                stream.stop_stream()
                self.wf.writeframes(b''.join(frames))
                self.wf.close()
                print('You should have a wav file in the current directory')
        stream.close()
        self.p.terminate()

    def callback(self, in_data, frame_count, time_info, status):
        frames.append(in_data)
        return (in_data, pyaudio.paContinue)

    def start_keyboard_listener(self):
        self.listener.start()
        print("Press 'space bar' to start recording.")
        print("Press 'Esc' to end recording.")


if __name__ == '__main__':
    recorder = AudioRecorder()
    recorder.start_keyboard_listener()
    recorder.start_record()
    print('End.')
