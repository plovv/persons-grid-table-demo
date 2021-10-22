package com.plovv.demopersonstable.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IPersonsTableView {

    enum ORDERING_TYPE {
        ASC, DESC
    }

    interface IViewActionHandler {
        void onSearchPerson(String searchTerm);
        void onPersonSelect(ArrayList<String> personRowData);
        void onSort(String column);
        void onViewCreated();
        void onViewDestroyed();
    }

    void setActionHandler(IViewActionHandler handler);

    void setPersonsList(ArrayList<ArrayList<String>> dataRows, ArrayList<String> columns, HashMap<String, Integer> columnWidths, int rowHeight);
    void updatePersonList(ArrayList<ArrayList<String>> dataRows);
    void updateSortColumnTitle(String column, ORDERING_TYPE ordering);
    void navigateToPersonDetails(int personIdArg);

    void showListProgress();
    void hideListProgress();
    void showMessage(String message);

}
