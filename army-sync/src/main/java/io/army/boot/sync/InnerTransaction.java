package io.army.boot.sync;

import io.army.tx.Transaction;

import java.sql.Statement;

interface InnerTransaction extends Transaction {

    Statement createStatement();
}
