package jenkem.persistence.data

import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.PrimaryKey

/**
 * Persistent IRC data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
class ImageIrc(var name: String, var irc: String) extends ImagePartTrait {
  @PrimaryKey var _id = name
  override def toString: String = name
}
