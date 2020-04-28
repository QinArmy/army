package io.army;

public interface SessionFactory extends GenericSessionFactory {

    Session currentSession() throws NoCurrentSessionException;


    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession();

        Session build() throws SessionException;

    }


}
