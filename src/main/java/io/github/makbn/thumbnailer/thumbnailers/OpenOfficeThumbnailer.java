/*
 * regain/Thumbnailer - A file search engine providing plenty of formats (Plugin)
 * Copyright (C) 2011  Come_IN Computerclubs (University of Siegen)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Come_IN-Team <come_in-team@listserv.uni-siegen.de>
 */

package io.github.makbn.thumbnailer.thumbnailers;


import io.github.makbn.thumbnailer.ThumbnailerException;
import io.github.makbn.thumbnailer.util.IOUtil;
import io.github.makbn.thumbnailer.util.ResizeImage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * This class extracts Thumbnails from OpenOffice-Files.
 * <p>
 * Depends:
 * <li> <i>NOT</i> on OpenOffice, as the Thumbnail is already inside the file. (184x256px regardless of page orientation)
 * (So if the thumbnail generation is not correct, it's OpenOffice's fault, not our's :-)
 */
public class OpenOfficeThumbnailer extends AbstractThumbnailer {

    private static final Logger logger = LoggerFactory.getLogger(OpenOfficeThumbnailer.class);
    private static final PDFBoxThumbnailer pdfBoxThumbnailer = new PDFBoxThumbnailer();

    @Override
    public void generateThumbnail(File input, File output) throws IOException, ThumbnailerException {
        //这里分为了两种情况，使用JODConverter 和openoffice转换成了pdf 和ods 两种文件
        if (FilenameUtils.getExtension(input.getName()).equalsIgnoreCase("pdf")) {
            pdfBoxThumbnailer.generateThumbnail(input, output);
        } else {
            BufferedInputStream in = null;
            ZipFile zipFile = null;

            try {
                //这里的input 是一个ods文件，ods文件中包含了图片以及缩略图的信息
                //这里还有以下内容可以完善，1均使用pdf的方式转换 参考https://blog.csdn.net/qq_33697094/article/details/113559873
                //2.使用coobrid/Thumbnailator 做进一步的处理（对图片做缩略图）
                //3.使用阿里的ffmpeg 生成视频的缩略图https://cloud.tencent.com/developer/article/1810395
                //4.使用淘宝使用的GraphicsMagick 做动态缩略图 http://blog.fangshuoit.com/?p=157
                zipFile = new ZipFile(input);
            } catch (ZipException e) {
                logger.warn("OpenOfficeThumbnailer", e);
                throw new ThumbnailerException("This is not a zipped file. Is this really an OpenOffice-File?", e);
            }

            try {
                ZipEntry entry = zipFile.getEntry("Thumbnails/thumbnail.png");
                if (entry == null)
                    throw new ThumbnailerException("Zip file does not contain 'Thumbnails/thumbnail.png' . Is this really an OpenOffice-File?");

                in = new BufferedInputStream(zipFile.getInputStream(entry));

                ResizeImage resizer = new ResizeImage(thumbWidth, thumbHeight);
                resizer.setInputImage(in);
                resizer.writeOutput(output);

                in.close();
            } finally {
                IOUtil.quietlyClose(in);
                IOUtil.quietlyClose(zipFile);
            }
        }
    }

    /**
     * Get a List of accepted File Types.
     * All OpenOffice Formats are accepted.
     *
     * @return MIME-Types
     */
    public String[] getAcceptedMIMETypes() {
        return new String[]{
                "application/vnd.sun.xml.writer",
                "application/vnd.sun.xml.writer.template",
                "application/vnd.sun.xml.writer.global",
                "application/vnd.sun.xml.calc",
                "application/vnd.sun.xml.calc.template",
                "application/vnd.stardivision.calc",
                "application/vnd.sun.xml.impress",
                "application/vnd.sun.xml.impress.template ",
                "application/vnd.stardivision.impress sdd",
                "application/vnd.sun.xml.draw",
                "application/vnd.sun.xml.draw.template",
                "application/vnd.stardivision.draw",
                "application/vnd.sun.xml.math",
                "application/vnd.stardivision.math",
                "application/vnd.oasis.opendocument.text",
                "application/vnd.oasis.opendocument.text-template",
                "application/vnd.oasis.opendocument.text-web",
                "application/vnd.oasis.opendocument.text-master",
                "application/vnd.oasis.opendocument.graphics",
                "application/vnd.oasis.opendocument.graphics-template",
                "application/vnd.oasis.opendocument.presentation",
                "application/vnd.oasis.opendocument.presentation-template",
                "application/vnd.oasis.opendocument.spreadsheet",
                "application/vnd.oasis.opendocument.spreadsheet-template",
                "application/vnd.oasis.opendocument.chart",
                "application/vnd.oasis.opendocument.formula",
                "application/vnd.oasis.opendocument.database",
                "application/vnd.oasis.opendocument.image",
                "text/html",
                "application/zip" /* Could be an OpenOffice file! */
        };
    }

}
