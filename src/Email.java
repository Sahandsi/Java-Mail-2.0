

import java.io.Serializable;

public class Email implements Serializable
{


    private String from, to, message ;
    private String attachment;
    private byte[] attachmentFile;

    public Email(String from, String to, String message,byte[] attachmentFile, String attachment)

    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.attachment = attachment;
        this.attachmentFile = attachmentFile;

    }



    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public String getMessage()
    {
        return message;
    }

    public String getAttachment()
    {
        return attachment;
    }

    public byte[] getAttachmentFiles()
    {
        return attachmentFile;
    }




}


