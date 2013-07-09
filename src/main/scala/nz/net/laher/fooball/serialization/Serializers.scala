package nz.net.laher.fooball.serialization

import org.json4s.CustomSerializer
import nz.net.laher.fooball.game.UserInput
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JField
import org.json4s.JsonAST.JInt
import org.json4s.JString
import org.json4s.FieldSerializer
import org.json4s.FieldSerializer._
import nz.net.laher.fooball.game.UserState
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.formats
import org.json4s.native.Serialization.read
import org.json4s.native.Serialization.write
import org.json4s.DefaultFormats

object Serializers {
      val longFormats = new DefaultFormats { 
    	  override val typeHints = ShortTypeHints(List(classOf[UserInput], classOf[UserState]))
      }

      val userInputSerializer = FieldSerializer[UserInput](
           FieldSerializer.renameTo("typ", "t") orElse FieldSerializer.renameTo("value", "v"),
           FieldSerializer.renameFrom("t", "typ") orElse FieldSerializer.renameFrom("v", "value"))
      val userStateSerializer = FieldSerializer[UserState](
           FieldSerializer.renameTo("keysDown", "kd"),
           FieldSerializer.renameFrom("kd", "keysDown"))
      val messageSerializer = FieldSerializer[Message](
           FieldSerializer.renameTo("components", "c"),
           FieldSerializer.renameFrom("c", "components"))
           
      val defaultFormats = new DefaultFormats {
		  override val typeHintFieldName = "jc"
		  override val typeHints = ShortTypeHints(List(classOf[UserInput],classOf[UserState]))
	  } + userInputSerializer + userStateSerializer + messageSerializer
	  
      
}