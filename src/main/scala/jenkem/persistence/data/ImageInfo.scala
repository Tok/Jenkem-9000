package jenkem.persistence.data

import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.PrimaryKey

/**
 * Persistent Info for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
class ImageInfo(var name: String, var icon: String,
    var method: String, var characters: String, var contrast: Short, var brightness: Short,
    var lines: Short, var lineWidth: Short, var creation: String) extends ImagePartTrait {
  @PrimaryKey var _id = name
  override def toString: String = name
}
