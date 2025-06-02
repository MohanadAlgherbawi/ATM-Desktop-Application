package com.example.javabuilder;

import java.io.*;
import java.util.ArrayList;

public class FileHandler implements Serializable {
    private ArrayList<UserAccount> accounts;

    public FileHandler() {
        try {
            accounts = loadAccounts();
        } catch (Exception e) {
            e.printStackTrace();
            accounts = new ArrayList<>();
        }
    }

    public ArrayList<UserAccount> loadAccounts() throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("accounts.bin")))) {
            return (ArrayList<UserAccount>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No existing file; start with an empty list
            return new ArrayList<>();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveAccounts() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("accounts.bin")))) {
            oos.writeObject(accounts);
        }
    }

    public ArrayList<UserAccount> getAccounts() {
        return accounts;
    }

    public boolean validateUserAccount(int accountId, String password) {
        if (findUserAccount(accountId).isLocked()) return false;
        for (UserAccount ua : accounts) {
            if (ua.getAccountId() == accountId && ua.getAccountPassword().equals(password)) {
                return true;
            }
        }


        return false;
    }
    public boolean validateFUserAccount(int accountId, double accountBalance) {
        if (findUserAccount(accountId).isLocked()) return false;
        for (UserAccount ua : accounts) {
            if (ua.getAccountId() == accountId && ua.getAccountBalance() == accountBalance) {
                return true;
            }
        }

        return false;
    }
    public boolean validateIUserAccount(int accountId) {
        for (UserAccount ua : accounts) {
            if (ua.getAccountId() == accountId) {
                return true;
            }
        }
        return false;
    }
    public UserAccount findUserAccount(int accountId) {
        for (UserAccount ua : accounts) {
            if (ua.getAccountId() == accountId) {
                return ua;
            }
        }
        return null;
    }

    public void addUserAccount(UserAccount userAccount) throws IOException {
        accounts.add(userAccount);
        saveAccounts();
    }
    public void updateUserAccount(UserAccount userAccount) throws IOException {
        accounts.set(accounts.indexOf(userAccount), userAccount);
        saveAccounts();
    }
    public void lockUserAccount(int accountId) throws IOException {
        findUserAccount(accountId).setLocked(true);
        updateUserAccount(findUserAccount(accountId));
    }
    public ArrayList<String> getDepositAccountHistory(int accountId) {
        return findUserAccount(accountId).getAccountDepositDate();
    }
    public ArrayList<String> getWithdrawAccountHistory(int accountId) {
        return findUserAccount(accountId).getAccountWithdrawalDate();
    }
}
