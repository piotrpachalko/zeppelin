/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.storage;

import org.apache.commons.io.IOUtils;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.interpreter.InterpreterInfoSaving;
import org.apache.zeppelin.notebook.NotebookAuthorizationInfoSaving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Storing config in local file system
 */
public class LocalConfigStorage extends ConfigStorage {

  private static Logger LOGGER = LoggerFactory.getLogger(LocalConfigStorage.class);

  private File interpreterSettingPath;
  private File authorizationPath;
  private File credentialPath;

  public LocalConfigStorage(ZeppelinConfiguration zConf) {
    super(zConf);
    this.interpreterSettingPath = new File(zConf.getInterpreterSettingPath());
    this.authorizationPath = new File(zConf.getNotebookAuthorizationPath());
    this.credentialPath = new File(zConf.getCredentialsPath());
  }

  @Override
  public void save(InterpreterInfoSaving settingInfos) throws IOException {
    LOGGER.info("Save Interpreter Setting to " + interpreterSettingPath.getAbsolutePath());
    writeToFile(settingInfos.toJson(), interpreterSettingPath);
  }

  @Override
  public InterpreterInfoSaving loadInterpreterSettings() throws IOException {
    if (!interpreterSettingPath.exists()) {
      LOGGER.warn("Interpreter Setting file {} is not existed", interpreterSettingPath);
      return null;
    }
    LOGGER.info("Load Interpreter Setting from file: " + interpreterSettingPath);
    String json = readFromFile(interpreterSettingPath);
    return buildInterpreterInfoSaving(json);
  }

  @Override
  public void save(NotebookAuthorizationInfoSaving authorizationInfoSaving) throws IOException {
    LOGGER.info("Save notebook authorization to file: " + authorizationPath);
    writeToFile(authorizationInfoSaving.toJson(), authorizationPath);
  }

  @Override
  public NotebookAuthorizationInfoSaving loadNotebookAuthorization() throws IOException {
    if (!authorizationPath.exists()) {
      LOGGER.warn("NotebookAuthorization file {} is not existed", authorizationPath);
      return null;
    }
    LOGGER.info("Load notebook authorization from file: " + authorizationPath);
    String json = readFromFile(authorizationPath);
    return NotebookAuthorizationInfoSaving.fromJson(json);
  }

  @Override
  public String loadCredentials() throws IOException {
    if (!credentialPath.exists()) {
      LOGGER.warn("Credential file {} is not existed", credentialPath);
      return null;
    }
    LOGGER.info("Load Credential from file: " + credentialPath);
    return readFromFile(credentialPath);
  }

  @Override
  public void saveCredentials(String credentials) throws IOException {
    LOGGER.info("Save Credentials to file: " + credentialPath);
    writeToFile(credentials, credentialPath);
  }

  private String readFromFile(File file) throws IOException {
    return IOUtils.toString(new FileInputStream(file));
  }

  private void writeToFile(String content, File file) throws IOException {
    FileOutputStream out = new FileOutputStream(file);
    IOUtils.write(content, out);
    out.close();
  }

}
