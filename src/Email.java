

import java.io.Serializable;

public class Email implements Serializable
{


    private String from, to, message ;
    private String attachment;
    private byte[] attachmentBytes;
   // private String name;


    public Email(String from, String to, String message,byte[] attachmentBytes, String attachment)

    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.attachment = attachment;
        this.attachmentBytes = attachmentBytes;
      //  this.name = name;

    }

//    public Attachment(byte[] attachmentBytes, String name)
//    {
//;
//    }

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

//    public String getName()
//    {
//        return this.name;
//    }

    public byte[] getAttachmentBytes()
    {
        return attachmentBytes;
    }




}


