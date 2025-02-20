package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AuthController {
    // Repository for BaseUser entries (stored in base_user.txt)
    private TxtModelRepository<BaseUser> baseUserRepository;
    // Repository for Customer User entries (stored in customer.txt)
    private TxtModelRepository<Customer> userRepository;
    // Repository for Admin entries (stored in admin.txt)
    private TxtModelRepository<Admin> adminRepository;
    // Repository for Vender entries (stored in vender.txt)
    private TxtModelRepository<Vender> venderRepository;
    // Repository for DeliveryRunner entries (stored in delivery_runner.txt)
    private TxtModelRepository<DeliveryRunner> deliveryRunnerRepository;

    private TxtModelRepository<Wallet> walletRepository;

    // Variable to hold the currently logged-in BaseUser
    private BaseUser currentUser;

    /**
     * Constructor initializes repositories for BaseUser, Customer, Admin, Vender, and DeliveryRunner.
     *
     * @throws IOException if there is an error initializing any repository.
     */
    public AuthController() throws IOException {
        this.baseUserRepository = new TxtModelRepository<>(
                "src/Data/base_user.txt",
                BaseUser::fromCsv,
                BaseUser::toCsv
        );
        this.userRepository = new TxtModelRepository<>(
                "src/Data/customer.txt",
                Customer::fromCsv,
                Customer::toCsv
        );
        this.adminRepository = new TxtModelRepository<>(
                "src/Data/admin.txt",
                Admin::fromCsv,
                Admin::toCsv
        );
        this.venderRepository = new TxtModelRepository<>(
                "src/Data/vender.txt",
                Vender::fromCsv,
                Vender::toCsv
        );
        this.deliveryRunnerRepository = new TxtModelRepository<>(
                "src/Data/delivery_runner.txt",
                DeliveryRunner::fromCsv,
                DeliveryRunner::toCsv
        );
        this.walletRepository = new TxtModelRepository<>(
                "src/Data/wallet.txt",
                Wallet::fromCsv,
                Wallet::toCsv
        );

        List<BaseUser> users = List.of();
        try {
            users = baseUserRepository.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (BaseUser user : users) {
            if (user.isCurrentUser()) {
                this.currentUser = user;
            }
        }
    }


    public List<BaseUser> getAllBaseusers(){
        try{
            return this.baseUserRepository.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initWallet(String baseUserId){
        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setBaseUserId(baseUserId);
        wallet.setId(UUID.randomUUID().toString());
        wallet.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        try {
            walletRepository.writeAll((List<Wallet>) wallet, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to log in a user by checking the provided phone (or email) and password.
     * If authentication is successful, sets the user as the current user and writes the update.
     *
     * @param email    the user's phone number or email address.
     * @param password the user's password.
     * @return true if login is successful; false otherwise.
     */
    public boolean login(String email, String password) {
        List<BaseUser> users;
        try {
            users = baseUserRepository.readAll();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        boolean loginSuccess = false;
        for (BaseUser user : users) {
            // Authenticate the user
            if (user.authenticate(email, password)) {
                user.setIsCurrentUser(true);
                this.currentUser = user; // Set the currently logged-in user
                loginSuccess = true;
            } else {
                user.setIsCurrentUser(false);
            }
        }

        // If login is successful, update the base_user.txt file
        if (loginSuccess) {
            try {
                baseUserRepository.writeAll(users, false);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return loginSuccess;
    }

    /**
     * Registers a new customer by creating corresponding entries in both base_user.txt and customer.txt.
     *
     * @param email     the customer's email address.
     * @param password  the customer's password.
     * @param firstName the customer's first name.
     * @param lastName  the customer's last name.
     * @param address   the customer's address.
     * @return true if registration is successful; false otherwise.
     */
    public boolean registerUser(String email, String password, String firstName, String lastName, String address) {
        try {
            // Read existing BaseUser records
            List<BaseUser> baseUsers = baseUserRepository.readAll();

            // Check if the email is already registered
            for (BaseUser baseUser : baseUsers) {
                if (baseUser.getEmailAddress().equals(email)) {
                    return false;
                }
            }

            // Generate unique ID and timestamp for the new user
            String baseUserId = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Create new BaseUser record with user type "customer"
            BaseUser newBaseUser = new BaseUser(baseUserId, email, password, "customer", createdAt, false, null, false);
            baseUsers.add(newBaseUser);
            baseUserRepository.writeAll(baseUsers, false);

            // Create corresponding Customer record
            List<Customer> customers = userRepository.readAll();
            String userId = UUID.randomUUID().toString();
            String iconImage = ""; // Initially empty
            Customer newCustomer = new Customer(userId, baseUserId, iconImage, firstName, lastName, address, createdAt);
            customers.add(newCustomer);
            userRepository.writeAll(customers, false);

            this.initWallet(baseUserId);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registers a new admin by creating corresponding entries in both base_user.txt and admin.txt.
     *
     * @param email     the admin's email address.
     * @param password  the admin's password.
     * @param firstName the admin's first name.
     * @param lastName  the admin's last name.
     * @return true if registration is successful; false otherwise.
     */
    public boolean registerAdmin(String email, String password, String firstName, String lastName) {
        try {
            // Read existing BaseUser records
            List<BaseUser> baseUsers = baseUserRepository.readAll();
            for (BaseUser baseUser : baseUsers) {
                if (baseUser.getEmailAddress().equals(email)) {
                    return false;
                }
            }

            // Generate unique ID and timestamp
            String baseUserId = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Create new BaseUser record with user type "admin"
            BaseUser newBaseUser = new BaseUser(baseUserId, email, password, "admin", createdAt, false, null, false);
            baseUsers.add(newBaseUser);
            baseUserRepository.writeAll(baseUsers, false);

            // Create corresponding Admin record
            List<Admin> admins = adminRepository.readAll();
            String adminId = UUID.randomUUID().toString();
            Admin newAdmin = new Admin(adminId, firstName, lastName, createdAt);
            admins.add(newAdmin);
            adminRepository.writeAll(admins, false);

            this.initWallet(baseUserId);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registers a new vender by creating corresponding entries in both base_user.txt and vender.txt.
     *
     * @param email                the vender's email address.
     * @param password             the vender's password.
     * @param storeName            the store name.
     * @param storeBackgroundImage the store's background image (can be empty).
     * @param storeIconImage       the store's icon image (can be empty).
     * @param storeDescription     the store's description.
     * @return true if registration is successful; false otherwise.
     */
    public boolean registerVender(String email, String password, String storeName, String storeBackgroundImage, String storeIconImage, String storeDescription) {
        try {
            // Read existing BaseUser records
            List<BaseUser> baseUsers = baseUserRepository.readAll();
            for (BaseUser baseUser : baseUsers) {
                if (baseUser.getEmailAddress().equals(email)) {
                    return false;
                }
            }

            // Generate unique ID and timestamp
            String baseUserId = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Create new BaseUser record with user type "vender"
            BaseUser newBaseUser = new BaseUser(baseUserId, email, password, "vender", createdAt, false, null, false);
            baseUsers.add(newBaseUser);
            baseUserRepository.writeAll(baseUsers, false);

            // Create corresponding Vender record
            List<Vender> venders = venderRepository.readAll();
            String venderId = UUID.randomUUID().toString();
            Vender newVender = new Vender(venderId, baseUserId, storeName, storeBackgroundImage, storeIconImage, storeDescription, createdAt);
            venders.add(newVender);
            venderRepository.writeAll(venders, false);

            this.initWallet(baseUserId);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registers a new delivery runner by creating corresponding entries in both base_user.txt and delivery_runner.txt.
     *
     * @param email     the delivery runner's email address.
     * @param password  the delivery runner's password.
     * @param firstName the delivery runner's first name.
     * @param lastName  the delivery runner's last name.
     * @return true if registration is successful; false otherwise.
     */
    public boolean registerDeliveryRunner(String email, String password, String firstName, String lastName) {
        try {
            // Read existing BaseUser records
            List<BaseUser> baseUsers = baseUserRepository.readAll();
            for (BaseUser baseUser : baseUsers) {
                if (baseUser.getEmailAddress().equals(email)) {
                    return false;
                }
            }

            // Generate unique ID and timestamp
            String baseUserId = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Create new BaseUser record with user type "delivery_runner"
            BaseUser newBaseUser = new BaseUser(baseUserId, email, password, "delivery_runner", createdAt, false, null, false);
            baseUsers.add(newBaseUser);
            baseUserRepository.writeAll(baseUsers, false);

            // Create corresponding DeliveryRunner record
            List<DeliveryRunner> runners = deliveryRunnerRepository.readAll();
            String runnerId = UUID.randomUUID().toString();
            DeliveryRunner newRunner = new DeliveryRunner(runnerId, baseUserId, firstName, lastName, createdAt);
            runners.add(newRunner);
            deliveryRunnerRepository.writeAll(runners, false);

            this.initWallet(baseUserId);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logs out the current user by setting the isCurrentUser flag to false for all users
     * and updating the base_user.txt file accordingly.
     */
    public void logout() {
        try {
            List<BaseUser> users = baseUserRepository.readAll();
            for (BaseUser user : users) {
                user.setIsCurrentUser(false);
            }
            baseUserRepository.writeAll(users, false);
            currentUser = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the currently logged-in BaseUser.
     *
     * @return the current BaseUser, or null if no user is logged in.
     */
    public BaseUser getCurrentUser() {
        return currentUser;
    }

    public boolean updateBaseUser(BaseUser updatedBaseUser) {
        try {
            List<BaseUser> allUsers = baseUserRepository.readAll();
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getId().equals(updatedBaseUser.getId())) {
                    allUsers.set(i, updatedBaseUser);
                    break;
                }
            }
            baseUserRepository.writeAll(allUsers, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public BaseUser getBaseUserById(String baseUserId) {
        try {
            // Read existing BaseUser records
            List<BaseUser> baseUsers = baseUserRepository.readAll();
            for (BaseUser baseUser : baseUsers) {
                if (baseUser.getId().equals(baseUserId)) {
                    return baseUser;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteBaseUser(String baseUserId) {
        try {
            List<BaseUser> allBaseUsers = baseUserRepository.readAll();
            for (int i = 0; i < allBaseUsers.size(); i++) {
                BaseUser bu = allBaseUsers.get(i);
                if (bu.getId().equals(baseUserId)) {
                    bu.setIsDeleted(true);
                    bu.setDeletedAt(LocalDateTime.now().toString());
                    allBaseUsers.set(i, bu);
                    break;
                }
            }
            baseUserRepository.writeAll(allBaseUsers, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}