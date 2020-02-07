from socket import *
from threading import Thread



def sendMsg(clientSocket):
    while True:
        msg = input('>>')
        clientSocket.send(msg.encode('utf8'))

def recvMsg(clientSocket):
    while True:
        msg = clientSocket.recv(1024)
        print('\r>>%s'%msg.decode('utf8'))

def main():

    clientSocket = socket(AF_INET,SOCK_STREAM)

    clientSocket.connect(('10.13.246.153',7788))

    tr = Thread(target=recvMsg,args=(clientSocket,)) #将套接字作为参数传给新线程，各自的线程中分别执行收，发数据
    ts = Thread(target=sendMsg,args=(clientSocket,))

    tr.start()
    ts.start()

if __name__ == '__main__':
    main()