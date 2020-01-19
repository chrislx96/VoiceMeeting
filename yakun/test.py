import threading
import time


class Test(threading.Thread):
    def run(self):
        pass


t = Test()
t.start()
print('1234')
