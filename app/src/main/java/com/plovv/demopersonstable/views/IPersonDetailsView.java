package com.plovv.demopersonstable.views;

public interface IPersonDetailsView {

    interface IViewActionHandler {
        void onViewCreated();
        void onViewDestroyed();
    }

    void setActionHandler(IViewActionHandler handler);

    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setAge(String age);
    void setEmail(String email);
    void setAddress(String address);
    void setPhoneNumber(String phone);

    void showListProgress();
    void hideListProgress();
    void showMessage(String message);

    int getPersonIdArg();

}
