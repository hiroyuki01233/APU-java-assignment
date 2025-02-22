package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.DeliveryRunner;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.TxtModelRepository;
import com.java_assignment.group.Model.Vender;

import java.io.IOException;
import java.util.List;

public class DeliveryRunnerController {
    private TxtModelRepository<DeliveryRunner> deliveryRunnerTxtModelRepository;
    private AuthController authController;
    private List<Order> orders;

    public DeliveryRunnerController() throws IOException {
        deliveryRunnerTxtModelRepository = new TxtModelRepository<>("src/Data/delivery_runner.txt", DeliveryRunner::fromCsv, DeliveryRunner::toCsv);
        TxtModelRepository<Order> orderRepository = new TxtModelRepository<>("src/Data/order.txt", Order::fromCsv, Order::toCsv);
        authController = new AuthController();
        this.orders = orderRepository.readAll();
    }


    public DeliveryRunner getDeliveryRunnerByBaseId(String baseUserId){
        try{
            for (DeliveryRunner runner : deliveryRunnerTxtModelRepository.readAll()) {
                if (runner.getBaseUserId().equals(baseUserId)){
                    return runner;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public DeliveryRunner getAvailableDeliveryRunner(){
        try {
            for (DeliveryRunner runner : deliveryRunnerTxtModelRepository.readAll()){
                Boolean available = true;
                for (Order order : orders){
                    if (
                            (
                                order.getCurrentStatus().equals("Ordered") ||
                                order.getCurrentStatus().equals("Preparing") ||
                                order.getCurrentStatus().equals("Preparing-runnerWaiting") ||
                                order.getCurrentStatus().equals("ReadyToPickup")
                            ) &&
                                order.getDeliveryRunnerId().equals(runner.getId())
                    ){
                        available = false;
                    }
                }

                if(available){
                    return runner;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Returns all venders.
     */
    public List<DeliveryRunner> getAllDeliveryRunner() {
        try{
            return deliveryRunnerTxtModelRepository.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the given vender record.
     */
    public boolean updateDeliveryRunner(DeliveryRunner updatedDeliveryRunner) {
        try {
            // Vender情報の更新
            List<DeliveryRunner> allDeliveryRunners = deliveryRunnerTxtModelRepository.readAll();
            for (int i = 0; i < allDeliveryRunners.size(); i++) {
                if (allDeliveryRunners.get(i).getId().equals(updatedDeliveryRunner.getId())) {
                    allDeliveryRunners.set(i, updatedDeliveryRunner);
                    break;
                }
            }
            deliveryRunnerTxtModelRepository.writeAll(allDeliveryRunners, false);

            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
