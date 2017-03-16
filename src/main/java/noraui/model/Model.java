package noraui.model;

public interface Model extends SerializableModel {

    /**
     * @return chidren class of noraui.model.ModelList
     */
    Class<? extends ModelList> getModelList();

}
