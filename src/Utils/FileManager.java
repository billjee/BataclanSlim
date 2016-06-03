/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author XPS 1645
 */
class FileManager
{
	public String myPath;
    //java doesn't need virtual
    public void OpenFile(String fileName) throws FileNotFoundException, IOException
    {
    	myPath = fileName;

    }

    //java doesn't need virtual
    public void CloseFile() throws IOException
    {

    }
}
