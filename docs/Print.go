package main

import (
	"bytes"
	"fmt"
	"log"
	"net"
	"os"
	"os/exec"
)

func main() {
	fmt.Println("-------------------------------------------------")
	fmt.Println("--------------       启动...       --------------")
	fmt.Println("--------------       启动成功      --------------")
	ipAddr()
	fmt.Println("-------------------------------------------------")

	javaJar()
}

func javaJar()  {
	var out bytes.Buffer

	cmd :=exec.Command("jre/bin/java", "-jar", "jar/print-server.jar");

	cmd.Stdout = &out
	err :=cmd.Run()

	if err !=nil {
		log.Println(err)
		return
	}
}

func ipAddr()  {
	addrs, err := net.InterfaceAddrs()

	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	for _, address := range addrs {
		// 检查ip地址判断是否回环地址
		if ipnet, ok := address.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ipnet.IP.To4() != nil {
				fmt.Println(ipnet.IP.String())
			}
		}
	}
}
