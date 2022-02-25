package com.blocksport.blocksport;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class Applicationcreate {

    String[] txheaders = {"X-Api-Key"};
    String[] txvalues = {"BbyoPjIuH94yqGA3jGJHhV9ClGyeeq37XvzJkQa7"};

    public AlgodClient client = null;

    private AlgodClient connectToNetwork() {
        final String Algod_Address = "https://testnet-algorand.api.purestake.io/ps2";
        final String Algod_Token = "BbyoPjIuH94yqGA3jGJHhV9ClGyeeq37XvzJkQa7";
        final Integer Algod_Port = 443;

        AlgodClient client = new AlgodClient(Algod_Address,Algod_Port,Algod_Token);
        return client;
    }
    public void waitForConfirmation(String txId) throws Exception {
        if(client == null)
            this.client = connectToNetwork();
        Long lastround = client.GetStatus().execute(txheaders,txvalues).body().lastRound;
        while (true) {
            try{
                Response<PendingTransactionResponse> pendingInfo = client.PendingTransactionInformation(txId).execute(txheaders,txvalues);
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound >0) {
                    System.out.println(" Transaction " + " confirmed in round" + pendingInfo.body().confirmedRound);
                    break;
                }
                lastround++;
                client.WaitForBlock(lastround).execute(txheaders,txvalues);
            } catch (Exception a) {
                throw (a);
            }
        }
    }
    public String compileProgram(AlgodClient client, byte[] programSource) {
        Response<CompileResponse> compileResponse = null;
        try {
            compileResponse = client.TealCompile().source(programSource).execute(txheaders,txvalues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(compileResponse.body().result);
        return compileResponse.body().result;
    }
    public Long createApp(AlgodClient client, Account creator, TEALProgram approvalProgram, TEALProgram clearProgram,
                          int globalInts, int globalBytes, int localInts, int localBytes) throws Exception {
        Address sender = creator.getAddress();
        TransactionParametersResponse params = client.TransactionParams().execute(txheaders,txvalues).body();
        Transaction txn = Transaction.ApplicationCreateTransactionBuilder().sender(sender)
                .suggestedParams(params).approvalProgram(approvalProgram)
                .clearStateProgram(clearProgram)
                .globalStateSchema(new StateSchema(globalBytes,globalInts))
                .localStateSchema(new StateSchema(localBytes,localInts))
                .build();

        SignedTransaction signedTxn = creator.signTransaction(txn);
        System.out.println("Signed transaction with txid: " + signedTxn.transactionID);

        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        String id = client.RawTransaction().rawtxn(encodedTxBytes).execute(txheaders,txvalues).body().txId;
        System.out.println("Successfully sent tx with Id: " + id );
        waitForConfirmation(id);

        PendingTransactionResponse pTrx = client.PendingTransactionInformation(id).execute(txheaders,txvalues).body();
        Long appId = pTrx.applicationIndex;
        System.out.println("Created new app-id: " + appId);
        return appId;
    }

    public void appcall() throws Exception{

        String creator_mnemonic = "caught salute abandon enroll certain anxiety gallery between bundle march frown once nerve subject outer vessel twin pudding room wedding cage park culture above unit";

        int localInts = 5;
        int localBytes = 5;
        int globalInts = 5;
        int globalBytes = 0;

        String approvalProgramSource = "#pragma version 5\n" +
                "txn ApplicationID\n" +
                "int 0\n" +
                "==\n" +
                "bnz main_l12\n" +
                "txn OnCompletion\n" +
                "int OptIn\n" +
                "==\n" +
                "bnz main_l11\n" +
                "txn OnCompletion\n" +
                "int CloseOut\n" +
                "==\n" +
                "bnz main_l10\n" +
                "txn OnCompletion\n" +
                "int UpdateApplication\n" +
                "==\n" +
                "bnz main_l9\n" +
                "txn OnCompletion\n" +
                "int DeleteApplication\n" +
                "==\n" +
                "bnz main_l8\n" +
                "txn OnCompletion\n" +
                "int NoOp\n" +
                "==\n" +
                "bnz main_l7\n" +
                "err\n" +
                "main_l7:\n" +
                "int 1\n" +
                "return\n" +
                "main_l8:\n" +
                "int 0\n" +
                "return\n" +
                "main_l9:\n" +
                "int 0\n" +
                "return\n" +
                "main_l10:\n" +
                "int 0\n" +
                "return\n" +
                "main_l11:\n" +
                "int 0\n" +
                "return\n" +
                "main_l12:\n" +
                "byte \"Account\"\n" +
                "app_global_get\n" +
                "store 0\n" +
                "byte \"Account\"\n" +
                "load 0\n" +
                "int 1\n" +
                "+\n" +
                "app_global_put\n" +
                "byte \"Name\"\n" +
                "app_global_get\n" +
                "store 0\n" +
                "byte \"Name\"\n" +
                "load 0\n" +
                "int 1\n" +
                "+\n" +
                "app_global_put\n" +
                "byte \"Id\"\n" +
                "app_global_get\n" +
                "store 0\n" +
                "byte \"Id\"\n" +
                "load 0\n" +
                "int 1\n" +
                "+\n" +
                "app_global_put\n" +
                "byte \"UserType\"\n" +
                "app_global_get\n" +
                "store 0\n" +
                "byte \"UserType\"\n" +
                "load 0\n" +
                "int 1\n" +
                "+\n" +
                "app_global_put\n" +
                "int 1\n" +
                "return";
        String clearProgramSource = "#pragma version 5\n" +
                "int 1\n";

        try{
            if (client == null)
                this.client = connectToNetwork();
            Account creatorAccount = new Account(creator_mnemonic);

            String approvalProgram = compileProgram(client, approvalProgramSource.getBytes("UTF-8"));
            String clearProgram = compileProgram(client, clearProgramSource.getBytes("UTF-8"));

            Long appId = createApp(client,creatorAccount,new TEALProgram(approvalProgram),new TEALProgram(clearProgram)
            ,globalBytes,globalInts,localBytes,localInts);



        } catch (Exception a){
            throw (a);
        }
    }
    public static void main (String[]args) throws Exception {
        Applicationcreate t = new Applicationcreate();
        t.appcall();
    }
}
