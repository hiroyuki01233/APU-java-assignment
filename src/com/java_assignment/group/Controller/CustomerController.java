package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.TxtModelRepository;
import com.java_assignment.group.Model.Customer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerController {
    private TxtModelRepository<Customer> repository;

    public CustomerController() throws IOException {
        repository = new TxtModelRepository<>("src/Data/customer.txt", Customer::fromCsv, Customer::toCsv);
    }

    /**
     * Returns all customers (BaseUser records with user_type "customer" that are not deleted).
     */
//    public List<BaseUser> getAllCustomers() throws IOException {
//        return repository.readAll().stream()
//                .filter(bu -> "customer".equals(bu.getUserType())
//                        && (bu.getIsDeleted() == null || !bu.getIsDeleted()))
//                .collect(Collectors.toList());
//    }
    public List<Customer> getAllCustomers() throws IOException {
        return repository.readAll();
    }

    /**
     * Updates the given customer record.
     */
    public boolean updateCustomer(Customer updatedCustomer) {
        try {
            List<Customer> allUsers = repository.readAll();
            for (int i = 0; i < allUsers.size(); i++) {
                Customer bu = allUsers.get(i);
                if (bu.getId().equals(updatedCustomer.getId())) {
                    allUsers.set(i, updatedCustomer);
                    break;
                }
            }
            repository.writeAll(allUsers, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

//    /**
//     * "Deletes" a customer by marking it as deleted.
//     */
//    public boolean deleteCustomer(String customerId) {
//        try {
//            List<Customer> allUsers = repository.readAll();
//            for (int i = 0; i < allUsers.size(); i++) {
//                Customer bu = allUsers.get(i);
//                if (bu.getId().equals(customerId)) {
//                    bu.setIsDeleted(true);
//                    bu.setDeletedAt(java.time.LocalDateTime.now().toString());
//                    allUsers.set(i, bu);
//                    break;
//                }
//            }
//            repository.writeAll(allUsers, false);
//            return true;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//    }

//    public Customer getCustomerByBaseUserId(String baseUserId) {
//        try {
//            // Read existing BaseUser records
//            List<Customer> customer = repository.readAll();
//            for (BaseUser baseUser : baseUsers) {
//                if (baseUser.getId().equals(baseUserId)) {
//                    return baseUser;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
