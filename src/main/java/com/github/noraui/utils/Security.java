package com.github.noraui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.github.noraui.exception.TechnicalException;

public class Security {

    public void createSha1CheckSumFile(File file) throws TechnicalException {
        try (PrintWriter sha1File = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath().replace(".properties", ".sha1"))))) {
            sha1File.print(createSha1(file));
        } catch (IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION), e);
        }
    }

    public String createSha1(File file) throws TechnicalException {
        try (InputStream fis = new FileInputStream(file);) {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    sha1.update(buffer, 0, n);
                }
            }
            return new HexBinaryAdapter().marshal(sha1.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION), e);
        }
    }

}
