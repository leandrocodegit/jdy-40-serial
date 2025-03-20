package org.example;
import purejavacomm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;

public class Main {

    private static boolean enviando = false;

    public static void main(String[] args) throws Exception {
        Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            System.out.println("Encontrado: " + portId.getName());
        }

        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier("COM5");
        SerialPort serialPort = (SerialPort) portId.open("JavaSerial", 2000);
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        InputStream inputStream = serialPort.getInputStream();
        OutputStream outputStream = serialPort.getOutputStream();

        Thread leituraThread = new Thread(() -> {
            try {
                int data;
                while ((data = inputStream.read()) > -1) {
                    System.out.print((char) data);
                }
            } catch (IOException e) {
                System.err.println("Erro na leitura: " + e.getMessage());
            }
        });
        leituraThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Digite um comando: ");
            String comando = scanner.nextLine();
            if (comando.equalsIgnoreCase("exit")) {
                break;
            }
            enviarComando(outputStream, comando);
        }

        scanner.close();
        serialPort.close();
        System.out.println("Fim do programa.");
    }

    private static void enviarComando(OutputStream outputStream, String comando) throws IOException {
        if (!enviando) {
            enviando = true;
            outputStream.write((comando + "\r\n").getBytes());
            outputStream.flush();
            enviando = false;
        }
    }
}