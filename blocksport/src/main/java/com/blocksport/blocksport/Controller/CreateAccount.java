package com.blocksport.blocksport.Controller;

import com.algorand.algosdk.account.Account;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping(headers = "Accept=application/json")
public class CreateAccount {

    @GetMapping("/blocksport/account")
    public ResponseEntity<String> createAccount() {

        try{
            Account myaccount = new Account();
            System.out.println("My Address:  " + myaccount.getAddress());
            System.out.println("My Mnemonic: " + myaccount.toMnemonic());
            Account account = new Account(myaccount.toMnemonic());



            return ResponseEntity.ok("account created      " +myaccount.getAddress());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok( e.getLocalizedMessage());
        }

    }
}



