from pynput import keyboard
from testRecord import audio_record
import threading


def on_press(key):
    pass

# t = threading.Thread(target=audio_record('./01.wav', 10), name='LoopThread')
def on_release(key):
    try:
        # start recording
        if key == keyboard.Key.space:
            # t.start()
            print('+++++++++++++++++')

        # stop recording
        elif key == keyboard.Key.esc:
            print('-----------------')
        # stop listener
        elif key.char == 'q':
            # t.join()
            return False

    except AttributeError:
        print('Please press valid command!')
        print('" space ": Start recording')
        print('" esc ": Stop recording')
        print('" q ": Stop Listener')


with keyboard.Listener(on_release=on_release) as listener:
    listener.join()
    print('================')
