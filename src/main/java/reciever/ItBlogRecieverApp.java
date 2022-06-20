package reciever;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.Scanner;

public class ItBlogRecieverApp {

    private static final String EXCHANGE_NAME = "it_blog";
    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        System.out.println("Enter command set_topic:");
        String topic = scanner.nextLine().substring("set_topic ".length());

        channel.queueBind(queueName, EXCHANGE_NAME, topic);
        System.out.println("Waiting for messages from topic = " + topic);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received '" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            String[] commandDetails = command.split(" ");

            if (commandDetails[0].equals("set_topic")) {
                channel.queueBind(queueName, EXCHANGE_NAME, commandDetails[1]);
                System.out.println("Waiting for messages from topic = " + commandDetails[1]);
            }

            if (commandDetails[0].equals("del_topic")) {
                channel.queueUnbind(queueName, EXCHANGE_NAME, commandDetails[1]);
                System.out.println("Stop waiting for messages from topic = " + commandDetails[1]);
            }
        }
    }
}
