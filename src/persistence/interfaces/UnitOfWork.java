package persistence.interfaces;

public interface UnitOfWork {
    void begin();
    void rollback();
    void commit();
}
