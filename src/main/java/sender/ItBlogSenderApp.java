package sender;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Scanner;

public class ItBlogSenderApp {

  private final static String EXCHANGE_NAME = "it_blog";
  private final static String ROUTING_KEY = "php";
  private final static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

      while (true) {
        System.out.println("Enter message:");
        String message = scanner.nextLine();
        if (message.equals("exit")) {
          System.out.println("Exit It-Blog Sender App");
          break;
        }
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
        System.out.println("Sent '" + message + "'");
      }
    }
  }
}
