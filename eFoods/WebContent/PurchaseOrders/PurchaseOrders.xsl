<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Purchase Order Report</title>
			</head>
			<body>
				<xsl:apply-templates/>
    		</body>
    	</html>
    </xsl:template>
    
    <xsl:template match="order">
    	<p>ID: <xsl:value-of select="@id"/></p>
    	<p>Time Submitted: <xsl:value-of select="@submitted"/></p>
    	
    	<xsl:apply-templates/>   
    	
    	<h3> Costs </h3>
    	<p>Total: <xsl:value-of select="./total"/></p>
    	<p>Shipping: <xsl:value-of select="./shipping"/></p>
    	<p>HST: <xsl:value-of select="./HST"/></p>
    	<p>Grand Total: <xsl:value-of select="./grandTotal"/></p>  		 	
    </xsl:template>
    
    <xsl:template match="customer">
    	<xsl:value-of select="./name"/>
    </xsl:template>
    		
    <xsl:template match="item">
	<tr>
	  <td><xsl:value-of select="./name"/></td>
	  <td><xsl:value-of select="./price"/></td>
	  <td><xsl:value-of select="./quantity"/></td>
	  <td><xsl:value-of select="./extended"/></td>
	</tr>
    </xsl:template>
    
</xsl:stylesheet>