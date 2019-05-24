




from socket import *

# server name is set the local ip
serverName = '127.0.0.1'
serverPort = 4400
BUFFER = 1024

if __name__ == "__main__":
    clientSocket = socket(AF_INET , SOCK_DGRAM)
    dead = False
    while not dead:
        mes = input("message ---> ")
        clientSocket.sendto(mes.encode('utf-8'), (serverName,serverPort))
        modifiedMes , serverAdd = clientSocket.recvfrom(BUFFER)
        print(modifiedMes)
        if(modifiedMes == '\n'):
            dead = True

    clientSocket.close()