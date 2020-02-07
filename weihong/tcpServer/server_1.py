# 要求
# 1. 使用tcp协议通信
# 2. 使用多进程配合多线程配合多线程的方式实现多个全双工的对话。

# 实现
# 1. 创建tcp的套接字，绑定，监听，变为被动
# 2. 每收到一次请求返回新的套接字，同时创建子进程，用来单独用新套接字对话
# 3. 子进程中创建两个线程，用来收发数据
import os
from socket import *
from multiprocessing import Process
from threading import Thread, local
import time


def worker(newSocket, destAdr):
    print('创建子会话进程成功..')
    # local_school = local() #创建ThreadLocal对象，用来储存各线程的局部变量
    ts = Thread(target=sendMsg, args=(newSocket,))
    tr = Thread(target=recvMsg, args=(newSocket,))

    ts.start()
    tr.start()

    ts.join()
    tr.join()


def sendMsg(newSocket):
    print('发送进程准备完毕！')
    print(os.getpid())
    while True:
        msg = str(os.getpid())
        newSocket.send(msg.encode('utf8'))
        time.sleep(5)


def ps_is_end():
    print('子进程结束')


def recvMsg(newSocket):
    print('接受进程准备完毕！')
    while True:
        msg = newSocket.recv(1024)
        if msg.decode('utf8') != '':  # 在ubantu中测试当客户端连接关闭时会自动发送‘’，而在windows下运行却会报错
            print('\r>>%s' % msg.decode('utf8'))
        else:
            print('%d下限了' % os.getpid())
            newSocket.close()
            break


def main():
    serverSocket = socket(AF_INET, SOCK_STREAM)

    serverSocket.bind(('', 9999))

    serverSocket.listen(5)

    while True:
        newSocket, destAdr = serverSocket.accept()
        p = Process(target=worker, args=(newSocket, destAdr,))

        p.start()
        newSocket.close()  # 拷贝到了新的进程中，这里的可以删掉了

    serverSocket.close()


if __name__ == '__main__':
    main()