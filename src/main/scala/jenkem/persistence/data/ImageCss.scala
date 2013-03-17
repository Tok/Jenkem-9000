package jenkem.persistence.data

import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.PrimaryKey

/**
 * Persistent CSS data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
class ImageCss(var name: String, var css: String) extends ImagePartTrait {
  @PrimaryKey var _id = name
  override def toString: String = name
}
