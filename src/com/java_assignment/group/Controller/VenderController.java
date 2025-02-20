package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Vender;
import com.java_assignment.group.Model.TxtModelRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class VenderController {
    private TxtModelRepository<Vender> venderRepository;
    private AuthController authController;

    public VenderController() throws IOException {
        venderRepository = new TxtModelRepository<>("src/Data/vender.txt", Vender::fromCsv, Vender::toCsv);
        authController = new AuthController();
    }

    /**
     * Returns all venders.
     */
    public List<Vender> getAllVenders() {
        try{
            return venderRepository.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vender getVenderByBaseUserId(String baseUserId) {
        try {
            // Vender情報の更新
            List<Vender> allVenders = venderRepository.readAll();
            for (int i = 0; i < allVenders.size(); i++) {
                if (allVenders.get(i).getBaseUserId().equals(baseUserId)) {
                    return allVenders.get(i);
                }
            }

            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the given vender record.
     */
    public boolean updateVender(Vender updatedVender) {
        try {
            // Vender情報の更新
            List<Vender> allVenders = venderRepository.readAll();
            for (int i = 0; i < allVenders.size(); i++) {
                if (allVenders.get(i).getId().equals(updatedVender.getId())) {
                    allVenders.set(i, updatedVender);
                    break;
                }
            }
            venderRepository.writeAll(allVenders, false);

            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}