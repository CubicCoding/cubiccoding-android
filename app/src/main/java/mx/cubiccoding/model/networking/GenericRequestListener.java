package mx.cubiccoding.model.networking;

/**
 * Created by martincazares on 8/16/16.
 */
public interface GenericRequestListener<T, E> {
    void onResult(T resultObj);
    void onFail(E failObj);
}
