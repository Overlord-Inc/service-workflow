/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inc.overloard.service.workflow;

import java.util.concurrent.BlockingQueue;
import lombok.Data;

/**
 *
 * @author achelian
 */
@Data
public class Endpoint {
    final String name;
    final BlockingQueue<Message> outgoing;
    final BlockingQueue<Message> incoming;
}
