public class URLByteEncoder {
    public static String encode(byte arr[])
    {
        String str = new String();
        for (int i=0;i<arr.length;i++)
        {
            int ival = Byte.toUnsignedInt(arr[i]);
            switch(ival)
            {
                case 45:
                case 95:
                case 46:
                case 126:
                    str+=(char)ival; break;
                default:
                    if ((ival>=65 && ival<=90)||(ival>=97 && ival<=122)||(ival>=48 && ival<=57))
                        str+=(char)ival;
                    else
                    {
                        String temp = Integer.toHexString(ival).toUpperCase();
                        if (temp.length()==1)
                            temp="0"+temp;
                        str+="%"+temp;
                    }
            }
        }

        return str;
    }
}
