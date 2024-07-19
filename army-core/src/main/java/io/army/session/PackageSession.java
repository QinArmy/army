package io.army.session;


/**
 * package interface
 */
non-sealed interface PackageSession extends Session {


    non-sealed interface PackageLocalSession extends LocalSession {

    }

    non-sealed interface PackageRmSession extends RmSession {

    }

}
