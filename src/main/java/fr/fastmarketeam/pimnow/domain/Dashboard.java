package fr.fastmarketeam.pimnow.domain;

public class Dashboard {
    private int nbCustomerActive;
    private int nbCustomerInactive;
    private int nbUserActive;
    private int nbUserInactive;
    private int nbProductCreated;
    private int nbProductDeleted;
    private int nbProductIntegrated;
    private Customer customer ;

    public Dashboard(Customer customer, int nbCustomerActive, int nbCustomerInactive, int nbProductCreated, int nbProductDeleted, int nbProductIntegrated, int nbUserInactive, int nbUserActive) {
        this.nbCustomerActive = nbCustomerActive ;
        this.nbCustomerInactive = nbCustomerInactive ;
        this.nbProductCreated = nbProductCreated;
        this.nbProductDeleted = nbProductDeleted;
        this.nbProductIntegrated = nbProductIntegrated;
        this.customer = customer ;
        this.nbUserActive = nbUserActive ;
        this.nbUserInactive = nbUserInactive ;
    }

    public int getNbProductCreated() {
        return nbProductCreated;
    }

    public void setNbProductCreated(int nbProductCreated) {
        this.nbProductCreated = nbProductCreated;
    }

    public int getNbProductDeleted() {
        return nbProductDeleted;
    }

    public void setNbProductDeleted(int nbProductDeleted) {
        this.nbProductDeleted = nbProductDeleted;
    }

    public int getNbProductIntegrated() {
        return nbProductIntegrated;
    }

    public void setNbProductIntegrated(int nbProductIntegrated) {
        this.nbProductIntegrated = nbProductIntegrated;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getNbUserActive() {
        return nbUserActive;
    }

    public void setNbUserActive(int nbUserActive) {
        this.nbUserActive = nbUserActive;
    }

    public int getNbUserInactive() {
        return nbUserInactive;
    }

    public void setNbUserInactive(int nbUserInactive) {
        this.nbUserInactive = nbUserInactive;
    }

    public int getNbCustomerActive() {
        return nbCustomerActive;
    }

    public void setNbCustomerActive(int nbCustomerActive) {
        this.nbCustomerActive = nbCustomerActive;
    }

    public int getNbCustomerInactive() {
        return nbCustomerInactive;
    }

    public void setNbCustomerInactive(int nbCustomerInactive) {
        this.nbCustomerInactive = nbCustomerInactive;
    }
}
