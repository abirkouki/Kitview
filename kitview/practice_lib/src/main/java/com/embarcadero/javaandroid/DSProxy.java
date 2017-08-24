// 
// Créé par le générateur de proxy DataSnap.
// 14/11/2016 10:22:55
// 

package com.embarcadero.javaandroid;

public class DSProxy {
  public static class TKitviewClass extends DSAdmin {
    public TKitviewClass(DSRESTConnection Connection) {
      super(Connection);
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_DeleteSessionFile_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_DeleteSessionFile_Metadata() {
      if (TKitviewClass_DeleteSessionFile_Metadata == null) {
        TKitviewClass_DeleteSessionFile_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("SessionFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.BooleanType, "Boolean"),
        };
      }
      return TKitviewClass_DeleteSessionFile_Metadata;
    }

    /**
     * @param SessionFilename [in] - Type on server: string
     * @return result - Type on server: Boolean
     */
    public boolean DeleteSessionFile(String SessionFilename) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.DeleteSessionFile");
      cmd.prepare(get_TKitviewClass_DeleteSessionFile_Metadata());
      cmd.getParameter(0).getValue().SetAsString(SessionFilename);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsBoolean();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_UploadFileInMultipleParts_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_UploadFileInMultipleParts_Metadata() {
      if (TKitviewClass_UploadFileInMultipleParts_Metadata == null) {
        TKitviewClass_UploadFileInMultipleParts_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("SessionFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("UploadedData", DSRESTParamDirection.Input, DBXDataTypes.JsonValueType, "TJSONObject"),
          new DSRESTParameterMetaData("PositionInFile", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_UploadFileInMultipleParts_Metadata;
    }

    /**
     * @param SessionFilename [in] - Type on server: string
     * @param UploadedData [in] - Type on server: TJSONObject
     * @param PositionInFile [in] - Type on server: Integer
     * @return result - Type on server: Integer
     */
    public int UploadFileInMultipleParts(String SessionFilename, TJSONObject UploadedData, int PositionInFile) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.POST);
      cmd.setText("TKitviewClass.UploadFileInMultipleParts");
      cmd.prepare(get_TKitviewClass_UploadFileInMultipleParts_Metadata());
      cmd.getParameter(0).getValue().SetAsString(SessionFilename);
      cmd.getParameter(1).getValue().SetAsJSONValue(UploadedData);
      cmd.getParameter(2).getValue().SetAsInt32(PositionInFile);
      getConnection().execute(cmd);
      return  cmd.getParameter(3).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_AddSessionFilenameToIdPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_AddSessionFilenameToIdPatient_Metadata() {
      if (TKitviewClass_AddSessionFilenameToIdPatient_Metadata == null) {
        TKitviewClass_AddSessionFilenameToIdPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPatient", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("SessionFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("lstAttributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("WithRefresh", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("WithPreview", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_AddSessionFilenameToIdPatient_Metadata;
    }

    /**
     * @param IdPatient [in] - Type on server: Integer
     * @param SessionFilename [in] - Type on server: string
     * @param lstAttributs [in] - Type on server: string
     * @param WithRefresh [in] - Type on server: Integer
     * @param WithPreview [in] - Type on server: Integer
     * @return result - Type on server: Integer
     */
    public int AddSessionFilenameToIdPatient(int IdPatient, String SessionFilename, String lstAttributs, int WithRefresh, int WithPreview) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.AddSessionFilenameToIdPatient");
      cmd.prepare(get_TKitviewClass_AddSessionFilenameToIdPatient_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPatient);
      cmd.getParameter(1).getValue().SetAsString(SessionFilename);
      cmd.getParameter(2).getValue().SetAsString(lstAttributs);
      cmd.getParameter(3).getValue().SetAsInt32(WithRefresh);
      cmd.getParameter(4).getValue().SetAsInt32(WithPreview);
      getConnection().execute(cmd);
      return  cmd.getParameter(5).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_AddObjectToIdPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_AddObjectToIdPatient_Metadata() {
      if (TKitviewClass_AddObjectToIdPatient_Metadata == null) {
        TKitviewClass_AddObjectToIdPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPatient", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("UploadedData", DSRESTParamDirection.Input, DBXDataTypes.JsonValueType, "TJSONObject"),
          new DSRESTParameterMetaData("lstAttributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("Name", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("WithRefresh", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_AddObjectToIdPatient_Metadata;
    }

    /**
     * @param IdPatient [in] - Type on server: Integer
     * @param UploadedData [in] - Type on server: TJSONObject
     * @param lstAttributs [in] - Type on server: string
     * @param Name [in] - Type on server: string
     * @param WithRefresh [in] - Type on server: Integer
     * @return result - Type on server: Integer
     */
    public int AddObjectToIdPatient(int IdPatient, TJSONObject UploadedData, String lstAttributs, String Name, int WithRefresh) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.POST);
      cmd.setText("TKitviewClass.AddObjectToIdPatient");
      cmd.prepare(get_TKitviewClass_AddObjectToIdPatient_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPatient);
      cmd.getParameter(1).getValue().SetAsJSONValue(UploadedData);
      cmd.getParameter(2).getValue().SetAsString(lstAttributs);
      cmd.getParameter(3).getValue().SetAsString(Name);
      cmd.getParameter(4).getValue().SetAsInt32(WithRefresh);
      getConnection().execute(cmd);
      return  cmd.getParameter(5).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_AddHttpObjectToIdPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_AddHttpObjectToIdPatient_Metadata() {
      if (TKitviewClass_AddHttpObjectToIdPatient_Metadata == null) {
        TKitviewClass_AddHttpObjectToIdPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPatient", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("Url", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("lstAttributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("Name", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("WithRefresh", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_AddHttpObjectToIdPatient_Metadata;
    }

    /**
     * @param IdPatient [in] - Type on server: Integer
     * @param Url [in] - Type on server: string
     * @param lstAttributs [in] - Type on server: string
     * @param Name [in] - Type on server: string
     * @param WithRefresh [in] - Type on server: Integer
     * @return result - Type on server: Integer
     */
    public int AddHttpObjectToIdPatient(int IdPatient, String Url, String lstAttributs, String Name, int WithRefresh) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.AddHttpObjectToIdPatient");
      cmd.prepare(get_TKitviewClass_AddHttpObjectToIdPatient_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPatient);
      cmd.getParameter(1).getValue().SetAsString(Url);
      cmd.getParameter(2).getValue().SetAsString(lstAttributs);
      cmd.getParameter(3).getValue().SetAsString(Name);
      cmd.getParameter(4).getValue().SetAsInt32(WithRefresh);
      getConnection().execute(cmd);
      return  cmd.getParameter(5).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetGabOutilAttributs_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetGabOutilAttributs_Metadata() {
      if (TKitviewClass_GetGabOutilAttributs_Metadata == null) {
        TKitviewClass_GetGabOutilAttributs_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdGabOutil", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("sep", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetGabOutilAttributs_Metadata;
    }

    /**
     * @param IdGabOutil [in] - Type on server: Integer
     * @param sep [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetGabOutilAttributs(int IdGabOutil, String sep) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetGabOutilAttributs");
      cmd.prepare(get_TKitviewClass_GetGabOutilAttributs_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdGabOutil);
      cmd.getParameter(1).getValue().SetAsString(sep);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCurrentIdPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCurrentIdPatient_Metadata() {
      if (TKitviewClass_GetCurrentIdPatient_Metadata == null) {
        TKitviewClass_GetCurrentIdPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_GetCurrentIdPatient_Metadata;
    }

    /**
     * @return result - Type on server: Integer
     */
    public int GetCurrentIdPatient() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCurrentIdPatient");
      cmd.prepare(get_TKitviewClass_GetCurrentIdPatient_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetBC_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetBC_Metadata() {
      if (TKitviewClass_GetBC_Metadata == null) {
        TKitviewClass_GetBC_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("SessionFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("aCmd", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("aParam", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("SendToKeyboard", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetBC_Metadata;
    }

    /**
     * @param SessionFilename [in] - Type on server: string
     * @param aCmd [in] - Type on server: string
     * @param aParam [in] - Type on server: string
     * @param SendToKeyboard [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String GetBC(String SessionFilename, String aCmd, String aParam, int SendToKeyboard) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetBC");
      cmd.prepare(get_TKitviewClass_GetBC_Metadata());
      cmd.getParameter(0).getValue().SetAsString(SessionFilename);
      cmd.getParameter(1).getValue().SetAsString(aCmd);
      cmd.getParameter(2).getValue().SetAsString(aParam);
      cmd.getParameter(3).getValue().SetAsInt32(SendToKeyboard);
      getConnection().execute(cmd);
      return  cmd.getParameter(4).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_ExecAFile_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_ExecAFile_Metadata() {
      if (TKitviewClass_ExecAFile_Metadata == null) {
        TKitviewClass_ExecAFile_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("aFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("Params", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.BooleanType, "Boolean"),
        };
      }
      return TKitviewClass_ExecAFile_Metadata;
    }

    /**
     * @param aFilename [in] - Type on server: string
     * @param Params [in] - Type on server: string
     * @return result - Type on server: Boolean
     */
    public boolean ExecAFile(String aFilename, String Params) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.ExecAFile");
      cmd.prepare(get_TKitviewClass_ExecAFile_Metadata());
      cmd.getParameter(0).getValue().SetAsString(aFilename);
      cmd.getParameter(1).getValue().SetAsString(Params);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsBoolean();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetProfilAttributs_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetProfilAttributs_Metadata() {
      if (TKitviewClass_GetProfilAttributs_Metadata == null) {
        TKitviewClass_GetProfilAttributs_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdProfil", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("sep", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetProfilAttributs_Metadata;
    }

    /**
     * @param IdProfil [in] - Type on server: Integer
     * @param sep [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetProfilAttributs(int IdProfil, String sep) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetProfilAttributs");
      cmd.prepare(get_TKitviewClass_GetProfilAttributs_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdProfil);
      cmd.getParameter(1).getValue().SetAsString(sep);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPersonnesReq_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPersonnesReq_Metadata() {
      if (TKitviewClass_GetPersonnesReq_Metadata == null) {
        TKitviewClass_GetPersonnesReq_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("Where", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetPersonnesReq_Metadata;
    }

    /**
     * @param Where [in] - Type on server: string
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetPersonnesReq(String Where) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPersonnesReq");
      cmd.prepare(get_TKitviewClass_GetPersonnesReq_Metadata());
      cmd.getParameter(0).getValue().SetAsString(Where);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPersonnes_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPersonnes_Metadata() {
      if (TKitviewClass_GetPersonnes_Metadata == null) {
        TKitviewClass_GetPersonnes_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetPersonnes_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetPersonnes() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPersonnes");
      cmd.prepare(get_TKitviewClass_GetPersonnes_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPersonneFromId_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPersonneFromId_Metadata() {
      if (TKitviewClass_GetPersonneFromId_Metadata == null) {
        TKitviewClass_GetPersonneFromId_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPersonne", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetPersonneFromId_Metadata;
    }

    /**
     * @param IdPersonne [in] - Type on server: Integer
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetPersonneFromId(int IdPersonne) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPersonneFromId");
      cmd.prepare(get_TKitviewClass_GetPersonneFromId_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPersonne);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPersonnesFromFormField_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPersonnesFromFormField_Metadata() {
      if (TKitviewClass_GetPersonnesFromFormField_Metadata == null) {
        TKitviewClass_GetPersonnesFromFormField_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("FieldName", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("FieldValue", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetPersonnesFromFormField_Metadata;
    }

    /**
     * @param FieldName [in] - Type on server: string
     * @param FieldValue [in] - Type on server: string
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetPersonnesFromFormField(String FieldName, String FieldValue) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPersonnesFromFormField");
      cmd.prepare(get_TKitviewClass_GetPersonnesFromFormField_Metadata());
      cmd.getParameter(0).getValue().SetAsString(FieldName);
      cmd.getParameter(1).getValue().SetAsString(FieldValue);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(2).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetGabarits_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetGabarits_Metadata() {
      if (TKitviewClass_GetGabarits_Metadata == null) {
        TKitviewClass_GetGabarits_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetGabarits_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetGabarits() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetGabarits");
      cmd.prepare(get_TKitviewClass_GetGabarits_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetGabDetails_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetGabDetails_Metadata() {
      if (TKitviewClass_GetGabDetails_Metadata == null) {
        TKitviewClass_GetGabDetails_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("GabaritId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetGabDetails_Metadata;
    }

    /**
     * @param GabaritId [in] - Type on server: Integer
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetGabDetails(int GabaritId) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetGabDetails");
      cmd.prepare(get_TKitviewClass_GetGabDetails_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(GabaritId);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetProfils_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetProfils_Metadata() {
      if (TKitviewClass_GetProfils_Metadata == null) {
        TKitviewClass_GetProfils_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetProfils_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetProfils() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetProfils");
      cmd.prepare(get_TKitviewClass_GetProfils_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCollectionCategories_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCollectionCategories_Metadata() {
      if (TKitviewClass_GetCollectionCategories_Metadata == null) {
        TKitviewClass_GetCollectionCategories_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetCollectionCategories_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetCollectionCategories() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCollectionCategories");
      cmd.prepare(get_TKitviewClass_GetCollectionCategories_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCurrentIdCollection_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCurrentIdCollection_Metadata() {
      if (TKitviewClass_GetCurrentIdCollection_Metadata == null) {
        TKitviewClass_GetCurrentIdCollection_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_GetCurrentIdCollection_Metadata;
    }

    /**
     * @return result - Type on server: Integer
     */
    public int GetCurrentIdCollection() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCurrentIdCollection");
      cmd.prepare(get_TKitviewClass_GetCurrentIdCollection_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCurrentIdCollectionCategory_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCurrentIdCollectionCategory_Metadata() {
      if (TKitviewClass_GetCurrentIdCollectionCategory_Metadata == null) {
        TKitviewClass_GetCurrentIdCollectionCategory_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_GetCurrentIdCollectionCategory_Metadata;
    }

    /**
     * @return result - Type on server: Integer
     */
    public int GetCurrentIdCollectionCategory() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCurrentIdCollectionCategory");
      cmd.prepare(get_TKitviewClass_GetCurrentIdCollectionCategory_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCollections_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCollections_Metadata() {
      if (TKitviewClass_GetCollections_Metadata == null) {
        TKitviewClass_GetCollections_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("CategoryId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetCollections_Metadata;
    }

    /**
     * @param CategoryId [in] - Type on server: Integer
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetCollections(int CategoryId) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCollections");
      cmd.prepare(get_TKitviewClass_GetCollections_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(CategoryId);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObject_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObject_Metadata() {
      if (TKitviewClass_GetObject_Metadata == null) {
        TKitviewClass_GetObject_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("ObjectId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("MaxWidth", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("MaxHeight", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetObject_Metadata;
    }

    /**
     * @param ObjectId [in] - Type on server: Integer
     * @param MaxWidth [in] - Type on server: Integer
     * @param MaxHeight [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String GetObject(int ObjectId, int MaxWidth, int MaxHeight) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObject");
      cmd.prepare(get_TKitviewClass_GetObject_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(ObjectId);
      cmd.getParameter(1).getValue().SetAsInt32(MaxWidth);
      cmd.getParameter(2).getValue().SetAsInt32(MaxHeight);
      getConnection().execute(cmd);
      return  cmd.getParameter(3).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetThumbnail_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetThumbnail_Metadata() {
      if (TKitviewClass_GetThumbnail_Metadata == null) {
        TKitviewClass_GetThumbnail_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("ObjectId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("MaxWidth", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("MaxHeight", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetThumbnail_Metadata;
    }

    /**
     * @param ObjectId [in] - Type on server: Integer
     * @param MaxWidth [in] - Type on server: Integer
     * @param MaxHeight [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String GetThumbnail(int ObjectId, int MaxWidth, int MaxHeight) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetThumbnail");
      cmd.prepare(get_TKitviewClass_GetThumbnail_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(ObjectId);
      cmd.getParameter(1).getValue().SetAsInt32(MaxWidth);
      cmd.getParameter(2).getValue().SetAsInt32(MaxHeight);
      getConnection().execute(cmd);
      return  cmd.getParameter(3).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectsForCollection_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectsForCollection_Metadata() {
      if (TKitviewClass_GetObjectsForCollection_Metadata == null) {
        TKitviewClass_GetObjectsForCollection_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("PatientId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("CollectionId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetObjectsForCollection_Metadata;
    }

    /**
     * @param PatientId [in] - Type on server: Integer
     * @param CollectionId [in] - Type on server: Integer
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetObjectsForCollection(int PatientId, int CollectionId) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectsForCollection");
      cmd.prepare(get_TKitviewClass_GetObjectsForCollection_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(PatientId);
      cmd.getParameter(1).getValue().SetAsInt32(CollectionId);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(2).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetFormats_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetFormats_Metadata() {
      if (TKitviewClass_GetFormats_Metadata == null) {
        TKitviewClass_GetFormats_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetFormats_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetFormats() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetFormats");
      cmd.prepare(get_TKitviewClass_GetFormats_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPatientIdentityId_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPatientIdentityId_Metadata() {
      if (TKitviewClass_GetPatientIdentityId_Metadata == null) {
        TKitviewClass_GetPatientIdentityId_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("PatientId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.Int32Type, "Integer"),
        };
      }
      return TKitviewClass_GetPatientIdentityId_Metadata;
    }

    /**
     * @param PatientId [in] - Type on server: Integer
     * @return result - Type on server: Integer
     */
    public int GetPatientIdentityId(int PatientId) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPatientIdentityId");
      cmd.prepare(get_TKitviewClass_GetPatientIdentityId_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(PatientId);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsInt32();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetForms_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetForms_Metadata() {
      if (TKitviewClass_GetForms_Metadata == null) {
        TKitviewClass_GetForms_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetForms_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetForms() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetForms");
      cmd.prepare(get_TKitviewClass_GetForms_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPatientFormResume_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPatientFormResume_Metadata() {
      if (TKitviewClass_GetPatientFormResume_Metadata == null) {
        TKitviewClass_GetPatientFormResume_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("PatientId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("FormName", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetPatientFormResume_Metadata;
    }

    /**
     * @param PatientId [in] - Type on server: Integer
     * @param FormName [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetPatientFormResume(int PatientId, String FormName) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPatientFormResume");
      cmd.prepare(get_TKitviewClass_GetPatientFormResume_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(PatientId);
      cmd.getParameter(1).getValue().SetAsString(FormName);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetSubscribers_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetSubscribers_Metadata() {
      if (TKitviewClass_GetSubscribers_Metadata == null) {
        TKitviewClass_GetSubscribers_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetSubscribers_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetSubscribers() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetSubscribers");
      cmd.prepare(get_TKitviewClass_GetSubscribers_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetServerTrace_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetServerTrace_Metadata() {
      if (TKitviewClass_GetServerTrace_Metadata == null) {
        TKitviewClass_GetServerTrace_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetServerTrace_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetServerTrace() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetServerTrace");
      cmd.prepare(get_TKitviewClass_GetServerTrace_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetAttributs_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetAttributs_Metadata() {
      if (TKitviewClass_GetAttributs_Metadata == null) {
        TKitviewClass_GetAttributs_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("TypeAttribut", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetAttributs_Metadata;
    }

    /**
     * @param TypeAttribut [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String GetAttributs(int TypeAttribut) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetAttributs");
      cmd.prepare(get_TKitviewClass_GetAttributs_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(TypeAttribut);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectsIds_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectsIds_Metadata() {
      if (TKitviewClass_GetObjectsIds_Metadata == null) {
        TKitviewClass_GetObjectsIds_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("Attributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetObjectsIds_Metadata;
    }

    /**
     * @param Attributs [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetObjectsIds(String Attributs) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectsIds");
      cmd.prepare(get_TKitviewClass_GetObjectsIds_Metadata());
      cmd.getParameter(0).getValue().SetAsString(Attributs);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectsIdsForPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectsIdsForPatient_Metadata() {
      if (TKitviewClass_GetObjectsIdsForPatient_Metadata == null) {
        TKitviewClass_GetObjectsIdsForPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPatient", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("Attributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetObjectsIdsForPatient_Metadata;
    }

    /**
     * @param IdPatient [in] - Type on server: Integer
     * @param Attributs [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetObjectsIdsForPatient(int IdPatient, String Attributs) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectsIdsForPatient");
      cmd.prepare(get_TKitviewClass_GetObjectsIdsForPatient_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPatient);
      cmd.getParameter(1).getValue().SetAsString(Attributs);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetFreePresentations_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetFreePresentations_Metadata() {
      if (TKitviewClass_GetFreePresentations_Metadata == null) {
        TKitviewClass_GetFreePresentations_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetFreePresentations_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetFreePresentations() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetFreePresentations");
      cmd.prepare(get_TKitviewClass_GetFreePresentations_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectInfo_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectInfo_Metadata() {
      if (TKitviewClass_GetObjectInfo_Metadata == null) {
        TKitviewClass_GetObjectInfo_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("ObjectId", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetObjectInfo_Metadata;
    }

    /**
     * @param ObjectId [in] - Type on server: Integer
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetObjectInfo(int ObjectId) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectInfo");
      cmd.prepare(get_TKitviewClass_GetObjectInfo_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(ObjectId);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetOrthalisRDV_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetOrthalisRDV_Metadata() {
      if (TKitviewClass_GetOrthalisRDV_Metadata == null) {
        TKitviewClass_GetOrthalisRDV_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("forDay", DSRESTParamDirection.Input, DBXDataTypes.DateType, "TDBXDate"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetOrthalisRDV_Metadata;
    }

    /**
     * @param forDay [in] - Type on server: TDBXDate
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetOrthalisRDV(int forDay) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetOrthalisRDV");
      cmd.prepare(get_TKitviewClass_GetOrthalisRDV_Metadata());
      cmd.getParameter(0).getValue().SetAsTDBXDate(forDay);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetPersonnesInfo_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetPersonnesInfo_Metadata() {
      if (TKitviewClass_GetPersonnesInfo_Metadata == null) {
        TKitviewClass_GetPersonnesInfo_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetPersonnesInfo_Metadata;
    }

    /**
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetPersonnesInfo() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetPersonnesInfo");
      cmd.prepare(get_TKitviewClass_GetPersonnesInfo_Metadata());
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(0).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectsInfoForPatient_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectsInfoForPatient_Metadata() {
      if (TKitviewClass_GetObjectsInfoForPatient_Metadata == null) {
        TKitviewClass_GetObjectsInfoForPatient_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdPatient", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("Attributs", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TKitviewClass_GetObjectsInfoForPatient_Metadata;
    }

    /**
     * @param IdPatient [in] - Type on server: Integer
     * @param Attributs [in] - Type on server: string
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetObjectsInfoForPatient(int IdPatient, String Attributs) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectsInfoForPatient");
      cmd.prepare(get_TKitviewClass_GetObjectsInfoForPatient_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdPatient);
      cmd.getParameter(1).getValue().SetAsString(Attributs);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(2).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_IsKitviewConnected_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_IsKitviewConnected_Metadata() {
      if (TKitviewClass_IsKitviewConnected_Metadata == null) {
        TKitviewClass_IsKitviewConnected_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.BooleanType, "Boolean"),
        };
      }
      return TKitviewClass_IsKitviewConnected_Metadata;
    }

    /**
     * @return result - Type on server: Boolean
     */
    public boolean IsKitviewConnected() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.IsKitviewConnected");
      cmd.prepare(get_TKitviewClass_IsKitviewConnected_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsBoolean();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_IsOrthalisConnected_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_IsOrthalisConnected_Metadata() {
      if (TKitviewClass_IsOrthalisConnected_Metadata == null) {
        TKitviewClass_IsOrthalisConnected_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.BooleanType, "Boolean"),
        };
      }
      return TKitviewClass_IsOrthalisConnected_Metadata;
    }

    /**
     * @return result - Type on server: Boolean
     */
    public boolean IsOrthalisConnected() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.IsOrthalisConnected");
      cmd.prepare(get_TKitviewClass_IsOrthalisConnected_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsBoolean();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetConfigVar_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetConfigVar_Metadata() {
      if (TKitviewClass_GetConfigVar_Metadata == null) {
        TKitviewClass_GetConfigVar_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("aVar", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetConfigVar_Metadata;
    }

    /**
     * @param aVar [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String GetConfigVar(int aVar) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetConfigVar");
      cmd.prepare(get_TKitviewClass_GetConfigVar_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(aVar);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetCollectionAttrs_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetCollectionAttrs_Metadata() {
      if (TKitviewClass_GetCollectionAttrs_Metadata == null) {
        TKitviewClass_GetCollectionAttrs_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdColl", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("sep", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetCollectionAttrs_Metadata;
    }

    /**
     * @param IdColl [in] - Type on server: Integer
     * @param sep [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetCollectionAttrs(int IdColl, String sep) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetCollectionAttrs");
      cmd.prepare(get_TKitviewClass_GetCollectionAttrs_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdColl);
      cmd.getParameter(1).getValue().SetAsString(sep);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetObjectAttributes_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetObjectAttributes_Metadata() {
      if (TKitviewClass_GetObjectAttributes_Metadata == null) {
        TKitviewClass_GetObjectAttributes_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("IdObject", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("sep", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetObjectAttributes_Metadata;
    }

    /**
     * @param IdObject [in] - Type on server: Integer
     * @param sep [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String GetObjectAttributes(int IdObject, String sep) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetObjectAttributes");
      cmd.prepare(get_TKitviewClass_GetObjectAttributes_Metadata());
      cmd.getParameter(0).getValue().SetAsInt32(IdObject);
      cmd.getParameter(1).getValue().SetAsString(sep);
      getConnection().execute(cmd);
      return  cmd.getParameter(2).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetDSServerVersion_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetDSServerVersion_Metadata() {
      if (TKitviewClass_GetDSServerVersion_Metadata == null) {
        TKitviewClass_GetDSServerVersion_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetDSServerVersion_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetDSServerVersion() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetDSServerVersion");
      cmd.prepare(get_TKitviewClass_GetDSServerVersion_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_GetOfficeInfos_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_GetOfficeInfos_Metadata() {
      if (TKitviewClass_GetOfficeInfos_Metadata == null) {
        TKitviewClass_GetOfficeInfos_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_GetOfficeInfos_Metadata;
    }

    /**
     * @return result - Type on server: string
     */
    public String GetOfficeInfos() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.GetOfficeInfos");
      cmd.prepare(get_TKitviewClass_GetOfficeInfos_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_ExecAScriptWithOutput_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_ExecAScriptWithOutput_Metadata() {
      if (TKitviewClass_ExecAScriptWithOutput_Metadata == null) {
        TKitviewClass_ExecAScriptWithOutput_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("aFilename", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("Params", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("Force32", DSRESTParamDirection.Input, DBXDataTypes.Int32Type, "Integer"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_ExecAScriptWithOutput_Metadata;
    }

    /**
     * @param aFilename [in] - Type on server: string
     * @param Params [in] - Type on server: string
     * @param Force32 [in] - Type on server: Integer
     * @return result - Type on server: string
     */
    public String ExecAScriptWithOutput(String aFilename, String Params, int Force32) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.ExecAScriptWithOutput");
      cmd.prepare(get_TKitviewClass_ExecAScriptWithOutput_Metadata());
      cmd.getParameter(0).getValue().SetAsString(aFilename);
      cmd.getParameter(1).getValue().SetAsString(Params);
      cmd.getParameter(2).getValue().SetAsInt32(Force32);
      getConnection().execute(cmd);
      return  cmd.getParameter(3).getValue().GetAsString();
    }
    
    
    private DSRESTParameterMetaData[] TKitviewClass_EchoString_Metadata;
    private DSRESTParameterMetaData[] get_TKitviewClass_EchoString_Metadata() {
      if (TKitviewClass_EchoString_Metadata == null) {
        TKitviewClass_EchoString_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("aVal", DSRESTParamDirection.Input, DBXDataTypes.WideStringType, "string"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.WideStringType, "string"),
        };
      }
      return TKitviewClass_EchoString_Metadata;
    }

    /**
     * @param aVal [in] - Type on server: string
     * @return result - Type on server: string
     */
    public String EchoString(String aVal) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TKitviewClass.EchoString");
      cmd.prepare(get_TKitviewClass_EchoString_Metadata());
      cmd.getParameter(0).getValue().SetAsString(aVal);
      getConnection().execute(cmd);
      return  cmd.getParameter(1).getValue().GetAsString();
    }
  }

  public static class TOrthalisClass extends DSAdmin {
    public TOrthalisClass(DSRESTConnection Connection) {
      super(Connection);
    }
    
    
    private DSRESTParameterMetaData[] TOrthalisClass_GetOrthalisRDV_Metadata;
    private DSRESTParameterMetaData[] get_TOrthalisClass_GetOrthalisRDV_Metadata() {
      if (TOrthalisClass_GetOrthalisRDV_Metadata == null) {
        TOrthalisClass_GetOrthalisRDV_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("forDay", DSRESTParamDirection.Input, DBXDataTypes.DateType, "TDBXDate"),
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.TableType, "TDBXReader"),
        };
      }
      return TOrthalisClass_GetOrthalisRDV_Metadata;
    }

    /**
     * @param forDay [in] - Type on server: TDBXDate
     * @return result - Type on server: TDBXReader
     */
    public TDBXReader GetOrthalisRDV(int forDay) throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TOrthalisClass.GetOrthalisRDV");
      cmd.prepare(get_TOrthalisClass_GetOrthalisRDV_Metadata());
      cmd.getParameter(0).getValue().SetAsTDBXDate(forDay);
      getConnection().execute(cmd);
      return (TDBXReader) cmd.getParameter(1).getValue().GetAsTable();
    }
    
    
    private DSRESTParameterMetaData[] TOrthalisClass_IsOrthalisConnected_Metadata;
    private DSRESTParameterMetaData[] get_TOrthalisClass_IsOrthalisConnected_Metadata() {
      if (TOrthalisClass_IsOrthalisConnected_Metadata == null) {
        TOrthalisClass_IsOrthalisConnected_Metadata = new DSRESTParameterMetaData[]{
          new DSRESTParameterMetaData("", DSRESTParamDirection.ReturnValue, DBXDataTypes.BooleanType, "Boolean"),
        };
      }
      return TOrthalisClass_IsOrthalisConnected_Metadata;
    }

    /**
     * @return result - Type on server: Boolean
     */
    public boolean IsOrthalisConnected() throws DBXException {
      DSRESTCommand cmd = getConnection().CreateCommand();
      cmd.setRequestType(DSHTTPRequestType.GET);
      cmd.setText("TOrthalisClass.IsOrthalisConnected");
      cmd.prepare(get_TOrthalisClass_IsOrthalisConnected_Metadata());
      getConnection().execute(cmd);
      return  cmd.getParameter(0).getValue().GetAsBoolean();
    }
  }

}
